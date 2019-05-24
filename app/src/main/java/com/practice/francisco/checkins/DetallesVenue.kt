 package com.practice.francisco.checkins

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detalles_venue.*
import kotlinx.android.synthetic.main.template_venues.*

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
        val venueActual = gson.fromJson(venueActualString, FoursquareAPIRequestVenues.Venue::class.java)
        //Log.d("venueActual", venueActual.name)

        tvNombre.text = venueActual.name
        tvState.text = venueActual.location?.state
        tvCountry.text = venueActual.location?.country
        tvCategory.text = venueActual.categories?.get(0)?.name
        tvCheckIn.text = venueActual.stats?.checkinsCount.toString()
        tvUsers.text = venueActual.stats?.usersCount.toString()
        tvTips.text = venueActual.stats?.tipCount.toString()


        //tvNombre.text = venueActual.name
    }
}
