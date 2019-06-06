package com.practice.francisco.checkins.Foursquare

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.foursquare.android.nativeoauth.FoursquareOAuth
import com.google.gson.Gson
import com.practice.francisco.checkins.Actividades.Login
import com.practice.francisco.checkins.Interfaces.*
import com.practice.francisco.checkins.Mensajes.Errores
import com.practice.francisco.checkins.Mensajes.Mensaje
import com.practice.francisco.checkins.Mensajes.Mensajes
import com.practice.francisco.checkins.Utilidades.Key
import com.practice.francisco.checkins.Utilidades.Network

class Foursquare(var activity: AppCompatActivity, var activityDestino: AppCompatActivity) {

    private val CODIGO_CONEXION = 200
    private val CODIGO_INTERCAMBIO_TOKEN = 201

    private val CLIENT_ID = Key.CLIENT_ID
    private val CLIENT_SECRET = Key.CLIENT_SECRET

    private val SETTINGS = "settings"
    private val ACCESS_TOKEN = "accessToken"

    private val URL_BASE = "https://api.foursquare.com/v2/"
    private val VERSION = "v=20190401"

    init {

    }

    fun iniciarSesion() {
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext, CLIENT_ID)

        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.NO_HAY_APP_FOURSQUARE
            )
            activity.startActivity(intent)
        } else {
            activity.startActivityForResult(intent, CODIGO_CONEXION)
        }
    }

    public fun validarActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODIGO_CONEXION -> {
                conexionCompleta(resultCode, data)
            }
            CODIGO_INTERCAMBIO_TOKEN -> {
                intercambioTokenCompleta(resultCode, data)
            }
        }

    }

    private fun conexionCompleta(resultCode: Int, data: Intent?) {
        val codigoRespuesta = FoursquareOAuth.getAuthCodeFromResult(resultCode, data)
        val exception = codigoRespuesta.exception

        if (exception == null) {
            val codigo = codigoRespuesta.code
            realizarIntercambioToken(codigo)
        } else {
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.ERROR_CONEXION_FSQR
            )
        }
    }

    private fun realizarIntercambioToken(codigo: String) {
        val intent =
            FoursquareOAuth.getTokenExchangeIntent(activity.applicationContext, CLIENT_ID, CLIENT_SECRET, codigo)
        activity.startActivityForResult(intent, CODIGO_INTERCAMBIO_TOKEN)
    }

    private fun intercambioTokenCompleta(resultCode: Int, data: Intent?) {
        val respuestaToken = FoursquareOAuth.getTokenFromResult(resultCode, data)
        val exception = respuestaToken.exception

        if (exception == null) {
            val accessToken = respuestaToken.accessToken
            if (!guardarToken(accessToken)) {
                Mensaje.mensajeError(
                    activity.applicationContext,
                    Errores.ERROR_GUARDAR_TOKEN
                )
                //navegarSiguienteActividad(activityDestino)
            } else {
                navegarSiguienteActividad()
            }


        } else {
            Mensaje.mensajeError(
                activity.applicationContext,
                Errores.ERROR_INTERCAMBIO_TOKEN
            )
        }
    }

    fun hayToken(): Boolean {
        return obtenerToken() != ""
    }

    fun obtenerToken(): String {
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val token = settings.getString(ACCESS_TOKEN, "")
        return token
    }

    fun cerrarSesion(){
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()

        editor.putString(ACCESS_TOKEN, "")
        editor.apply()
    }

    fun regresarIniciarSesion(){
        activity.startActivity(Intent(this.activity, Login::class.java))
        activity.finish()
    }

    private fun guardarToken(token: String): Boolean {
        if (token.isEmpty()) {
            return false
        }
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()

        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
        return true
    }

    fun navegarSiguienteActividad() {
        activity.startActivity(Intent(this.activity, activityDestino::class.java))
        activity.finish()
    }

    fun obtenerVenues(lat: String, lon: String, obtenerVenuesInterface: ObtenerVenuesInterface) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=" + lat + "," + lon
        val token = "oauth_token=" + obtenerToken()
        var url = URL_BASE + seccion + metodo + "?" + ll + "&" + token + "&" + VERSION
        network.httpRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)
                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues!!
                if (meta?.code == 200) {
                    //enviar mensaje exito
                    obtenerVenuesInterface.venuesGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

    fun nuevoCheckin(id: String, location: Location, mensaje: String) {
        val network = Network(activity)
        val seccion = "checkins/"
        val metodo = "add"
        val token = "oauth_token=" + obtenerToken()
        val query =
            "?venueId=" + id + "&shout" + mensaje + "&ll=" + location.lat.toString() + "," + location.lng.toString() + "&" + token + "&" + VERSION
        val url = URL_BASE + seccion + metodo + query

        network.httpPostRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                Log.d("nuevoCheckin", response)
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)
                var meta = objetoRespuesta.meta

                if (meta?.code == 200) {
                    //enviar mensaje exito
                    Mensaje.mensaje(activity.applicationContext, Mensajes.CHECKIN_SUCCESS)
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })

    }

    fun obtenerUsuarioActual(usuarioActualInterface: UsuariosInterface){
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self"
        val token = "oauth_token=" + obtenerToken()
        val query ="?"+ token + "&" + VERSION
        val url = URL_BASE + seccion + metodo + query

        network.httpRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPISelfUser::class.java)
                var meta = objetoRespuesta.meta

                if (meta?.code == 200) {
                    usuarioActualInterface.obtenerUsuarioActual(objetoRespuesta.response?.user!!)
                    //enviar mensaje exito
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

    fun cargarCategorias(categoriasInterface:CategoriasVenuesInterface){
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "categories/"
        val token = "oauth_token=" + obtenerToken()
        val query ="?"+ token + "&" + VERSION
        val url = URL_BASE + seccion + metodo + query

        network.httpRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, FoursquareAPICategorias::class.java)
                var meta = objetoRespuesta.meta

                if (meta?.code == 200) {
                    categoriasInterface.categoriasVenues(objetoRespuesta.response?.categories!!)
                    //enviar mensaje exito
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

    fun obtenerVenues(lat: String, lon: String, categoryId:String, obtenerVenuesInterface: ObtenerVenuesInterface) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "search/"
        val ll = "ll=" + lat + "," + lon
        var categoria = "categoryId="+categoryId
        val token = "oauth_token=" + obtenerToken()
        var url = URL_BASE + seccion + metodo + "?" + ll + "&" + categoria+"&"+ token + "&" + VERSION
        network.httpRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, FoursquareAPIRequestVenues::class.java)
                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues!!
                if (meta?.code == 200) {
                    //enviar mensaje exito
                    obtenerVenuesInterface.venuesGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

    fun nuevoLike(id: String) {
        val network = Network(activity)
        val seccion = "venues/"
        val metodo = "like/"
        val token = "oauth_token=" + obtenerToken()
        val query ="?" + token + "&" + VERSION
        val url = URL_BASE + seccion + id + "/"+ metodo + query

        network.httpPostRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                //Log.d("nuevoLike", response)
                val gson = Gson()
                val objetoRespuesta = gson.fromJson(response, LikeResponse::class.java)
                var meta = objetoRespuesta.meta

                if (meta?.code == 200) {
                    //enviar mensaje exito
                    Mensaje.mensaje(activity.applicationContext, Mensajes.LIKE_SUCCESS)
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

    fun obtenerVenuesDeLike(venuesForLikeInterface:VenuesForLikeInterface){
        val network = Network(activity)
        val seccion = "users/"
        val metodo = "self/"
        val token = "oauth_token=" + obtenerToken()
        var url = URL_BASE + seccion + metodo + "venuelikes?limit=10&"+ token + "&" + VERSION
        network.httpRequest(activity.applicationContext, url, object :
            HTTPResponse {
            override fun httpResponseSuccess(response: String) {
                var gson = Gson()
                var objetoRespuesta = gson.fromJson(response, VenuesDeLikes::class.java)
                var meta = objetoRespuesta.meta
                var venues = objetoRespuesta.response?.venues?.items!!
                if (meta?.code == 200) {
                    //enviar mensaje exito
                    venuesForLikeInterface.venuesGenerados(venues)
                } else {
                    if (meta?.code == 400) {
                        //problema coordenadas
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            meta?.errorDetail
                        )
                    } else {
                        Mensaje.mensajeError(
                            activity.applicationContext,
                            Errores.ERROR_QUERY
                        )
                    }
                }
            }
        })
    }

}