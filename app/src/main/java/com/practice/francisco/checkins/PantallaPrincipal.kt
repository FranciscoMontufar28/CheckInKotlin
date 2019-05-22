package com.practice.francisco.checkins

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationResult

class PantallaPrincipal : AppCompatActivity() {

    var ubicacion: Ubicacion? = null
    var foursquare: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        foursquare = Foursquare(this, this)
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
                    foursquare?.obtenerVenues(lat, lon, object : ObtenerVenuesInterface{
                        override fun venuesGenerados(venues: ArrayList<FoursquareAPIRequestVenues.Venue>) {
                            for (venue in venues){
                                Log.d("VENUE", venue.name)
                            }
                        }
                    })
                }
            })
        }
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
}
