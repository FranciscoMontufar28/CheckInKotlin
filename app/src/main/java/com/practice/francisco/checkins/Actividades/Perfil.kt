package com.practice.francisco.checkins.Actividades

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.practice.francisco.checkins.Foursquare.Checkins
import com.practice.francisco.checkins.Foursquare.Foursquare
import com.practice.francisco.checkins.Foursquare.User
import com.practice.francisco.checkins.Foursquare.Venue
import com.practice.francisco.checkins.Interfaces.UsuariosInterface
import com.practice.francisco.checkins.Interfaces.VenuesForLikeInterface
import com.practice.francisco.checkins.R

class Perfil : AppCompatActivity() {

    ///Toolbar
    var toolbar: Toolbar? = null
    var foursquare: Foursquare? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvFriends = findViewById<TextView>(R.id.tvFriends)
        val tvTips = findViewById<TextView>(R.id.tvTips)
        val tvPhotos = findViewById<TextView>(R.id.tvFotos)

        foursquare = Foursquare(this, this)

        //initToolbar("nombre")

        if (foursquare?.hayToken()!!) {
            foursquare?.obtenerUsuarioActual(object : UsuariosInterface{
                override fun obtenerUsuarioActual(usuario: User) {
                    tvNombre.text = usuario.firstName
                    tvFriends.text = String.format("%d %s",usuario.friends?.count, getString(R.string.app_perfil_amigos))
                    tvPhotos.text = String.format("%d %s",usuario.photos?.count,getString(R.string.app_perfil_fotos))
                    tvTips.text = String.format("%d %s",usuario.tips?.count,getString(R.string.app_perfil_tips))
                    initToolbar(usuario.firstName + " "+ usuario.lastName)
                }
            })
        }else{
            foursquare?.regresarIniciarSesion()
        }
    }

    fun initToolbar(nombrePerfil: String){
        toolbar = findViewById(R.id.appToolbar)
        toolbar?.setTitle(R.string.app_likes)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener { finish() }
    }
}
