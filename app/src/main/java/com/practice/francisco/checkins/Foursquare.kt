package com.practice.francisco.checkins

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.foursquare.android.nativeoauth.FoursquareOAuth

class Foursquare(var activity: AppCompatActivity, var activityDestino: AppCompatActivity) {

    private val CODIGO_CONEXION = 200
    private val CODIGO_INTERCAMBIO_TOKEN = 201

    private val CLIENT_ID = ""
    private val CLIENT_SECRET = ""

    private val SETTINGS = "settings"
    private val ACCESS_TOKEN = "accessToken"

    init {

    }

    fun iniciarSesion() {
        val intent = FoursquareOAuth.getConnectIntent(activity.applicationContext, CLIENT_ID)

        if (FoursquareOAuth.isPlayStoreIntent(intent)) {
            Mensaje.mensajeError(activity.applicationContext, Errores.NO_HAY_APP_FOURSQUARE)
            activity.startActivity(intent)
        } else {
            activity.startActivityForResult(intent, CODIGO_CONEXION)
        }
    }

    private fun validarActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODIGO_CONEXION -> {
                conexionCompleta(requestCode, data)
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
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_CONEXION_FSQR)
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
                Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_GUARDAR_TOKEN)
            } else {
                navegarSiguienteActividad(activityDestino)
            }


        } else {
            Mensaje.mensajeError(activity.applicationContext, Errores.ERROR_INTERCAMBIO_TOKEN)
        }
    }

    private fun hayToken(): Boolean {
        return obtenerToken() != ""
    }

    fun obtenerToken(): String {
        val settings = activity.getSharedPreferences(SETTINGS, 0)
        val token = settings.getString(ACCESS_TOKEN, "")
        return token
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

    private fun navegarSiguienteActividad(activityDestino: AppCompatActivity){
        activity.startActivity(Intent(this.activity, activityDestino::class.java))
        activity.finish()
    }
}