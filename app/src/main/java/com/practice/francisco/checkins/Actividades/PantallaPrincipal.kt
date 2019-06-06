package com.practice.francisco.checkins.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import com.practice.francisco.checkins.*
import com.practice.francisco.checkins.Foursquare.Foursquare
import com.practice.francisco.checkins.Foursquare.Venue
import com.practice.francisco.checkins.Interfaces.ObtenerVenuesInterface
import com.practice.francisco.checkins.Interfaces.UbicacionListener
import com.practice.francisco.checkins.Interfaces.VenuesForLikeInterface
import com.practice.francisco.checkins.RecyclerViewPrincipal.AdaptadorCustom
import com.practice.francisco.checkins.RecyclerViewPrincipal.ClickListener
import com.practice.francisco.checkins.RecyclerViewPrincipal.LongClickListener
import com.practice.francisco.checkins.Utilidades.Ubicacion

class PantallaPrincipal : AppCompatActivity() {

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? =null
    var layoutManager: RecyclerView.LayoutManager? = null

    var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null

    ///Toolbar
    var toolbar:Toolbar? = null

    companion object {
        val VENUE_ACTUAL = "com.practice.francisco.checkins.Actividades.PantallaPrincipal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        foursquare = Foursquare(this, this)

        lista = findViewById(R.id.rvLugares)
        lista?.setHasFixedSize(true)

        initToolbar()

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        if (foursquare?.hayToken()!!) {
            ubicacion = Ubicacion(this, object : UbicacionListener {
                override fun ubicacionResponse(locationResult: LocationResult) {
                    val lat = locationResult.lastLocation.latitude.toString()
                    val lon = locationResult.lastLocation.longitude.toString()
                    /*Toast.makeText(
                        applicationContext,
                        locationResult.lastLocation.latitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()*/
                    foursquare?.obtenerVenues(lat, lon, object :
                        ObtenerVenuesInterface {
                        override fun venuesGenerados(venues: ArrayList<Venue>) {
                            implementacionRecyclerView(venues)
                            for (venue in venues) {
                                Log.d("VENUE", venue.name)
                            }
                        }
                    })
                }
            })
        }else{
            foursquare?.regresarIniciarSesion()
        }
    }

    private fun implementacionRecyclerView(lugares:ArrayList<Venue>){
        adaptador = AdaptadorCustom(lugares, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                //Toast.makeText(applicationContext, lugares.get(index).name, Toast.LENGTH_SHORT).show()
                val venueToJson = Gson()
                val venueActualString = venueToJson.toJson(lugares.get(index))
                val intent = Intent(applicationContext, DetallesVenue::class.java)
                intent.putExtra(VENUE_ACTUAL, venueActualString)
                startActivity(intent)
            }
        },object: LongClickListener {
            override fun longClick(vista: View, index: Int) {
                /*if(!isActionMode){
                    startSupportActionMode(callback)
                    isActionMode = true
                    adaptador?.seleccionarItem(index)
                }else{
                    //hacer selecciones o deselecciones
                    adaptador?.seleccionarItem(index)
                }
                actionMode?.title = adaptador?.obtenerNumeroElementosSeleccionados().toString()+" seleccionados"
                */
            }

        })
        lista?.adapter = adaptador
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        ubicacion?.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        ubicacion?.inicializarUbicacion()
    }

    override fun onPause() {
        super.onPause()
        ubicacion?.detenerActualizacionUbicacion()
    }

    fun initToolbar(){
        toolbar = findViewById(R.id.appToolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.iconoCategorias -> {
                val intent = Intent(this, Categorias::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoFavoritos->{
                val intent = Intent(this, Likes::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoPerfil->{
                val intent = Intent(this, Perfil ::class.java)
                startActivity(intent)
                return true
            }
            R.id.iconoCerrarSesion->{
                foursquare?.cerrarSesion()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

    }
}
