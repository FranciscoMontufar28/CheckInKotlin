 package com.practice.francisco.checkins.Actividades

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.practice.francisco.checkins.Foursquare.Foursquare
import com.practice.francisco.checkins.R
import com.practice.francisco.checkins.Foursquare.Venue
import kotlinx.android.synthetic.main.activity_detalles_venue.*
import java.net.URLEncoder

 class DetallesVenue : AppCompatActivity() {

     ///Toolbar
     var toolbar: Toolbar? = null
     var bCheckin:Button? = null
     var bLike:Button? = null

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

        initToolbar(venueActual.name)
        bCheckin = findViewById(R.id.bCheckin)
        bLike = findViewById(R.id.bLike)
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

        /*if (foursquare.hayToken()){
            //foursquare.nuevoCheckin(venueActual.id, venueActual.location!!, "Hola%20mundo")
            foursquare.obtenerUsuarioActual(object : UsuariosInterface {
                override fun obtenerUsuarioActual(usuario: User) {
                    Toast.makeText(applicationContext, usuario.firstName, Toast.LENGTH_SHORT).show()
                }

            })

        }*/

        bCheckin?.setOnClickListener {
            if (foursquare.hayToken()) {
                val etMensaje = EditText(this)
                etMensaje.hint = "Hola"

                AlertDialog.Builder(this)
                    .setTitle("Nuevo Checkin")
                    .setMessage("Ingresa un nuevo mensaje")
                    .setView(etMensaje)
                    .setPositiveButton("Check-in") { dialog, which ->
                        val mensaje =URLEncoder.encode(etMensaje.text.toString(),"UTF-8")
                        foursquare.nuevoCheckin(venueActual.id, venueActual.location!!, mensaje)
                    }
                    .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->  })
                    .show()

            }else{
                foursquare?.regresarIniciarSesion()
            }
        }

        bLike?.setOnClickListener {
            if (foursquare.hayToken()) {
                foursquare.nuevoLike(venueActual.id)
            }
        }
    }

     fun initToolbar(categoria: String){
         toolbar = findViewById(R.id.appToolbar)
         toolbar?.setTitle(categoria)
         setSupportActionBar(toolbar)
         var actionBar = supportActionBar
         actionBar?.setDisplayHomeAsUpEnabled(true)

         toolbar?.setNavigationOnClickListener { finish() }
     }
}
