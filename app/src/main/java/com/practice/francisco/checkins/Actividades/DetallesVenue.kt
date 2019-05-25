 package com.practice.francisco.checkins.Actividades

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.practice.francisco.checkins.Foursquare.Foursquare
import com.practice.francisco.checkins.Interfaces.UsuariosInterface
import com.practice.francisco.checkins.R
import com.practice.francisco.checkins.Foursquare.User
import com.practice.francisco.checkins.Foursquare.Venue
import kotlinx.android.synthetic.main.activity_detalles_venue.*

 class DetallesVenue : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_venue)

        val tvNombre = tvName
        val tvState = tvState
        val tvCountry = tvCountry
        val tvCategory = tvCategory
        val tvCheckIn = tvCheckIn
        val tvUsers = tvUser
        val tvTips = tvTips

        val venueActualString = intent.getStringExtra(PantallaPrincipal.VENUE_ACTUAL)
        val gson = Gson()
        val venueActual = gson.fromJson(venueActualString, Venue::class.java)
        //Log.d("venueActual", venueActual.name)

        tvNombre.text = venueActual.name
        tvState.text = venueActual.location?.state
        tvCountry.text = venueActual.location?.country
        tvCategory.text = venueActual.categories?.get(0)?.name
        tvCheckIn.text = venueActual.stats?.checkinsCount.toString()
        tvUsers.text = venueActual.stats?.usersCount.toString()
        tvTips.text = venueActual.stats?.tipCount.toString()

        val foursquare = Foursquare(
            this,
            DetallesVenue()
        )

        if (foursquare.hayToken()){
            //foursquare.nuevoCheckin(venueActual.id, venueActual.location!!, "Hola%20mundo")
            foursquare.obtenerUsuarioActual(object : UsuariosInterface {
                override fun obtenerUsuarioActual(usuario: User) {
                    Toast.makeText(applicationContext, usuario.firstName, Toast.LENGTH_SHORT).show()
                }

            })

        }
    }
}
