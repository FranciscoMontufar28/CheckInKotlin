package com.practice.francisco.checkins.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.practice.francisco.checkins.Foursquare.Category
import com.practice.francisco.checkins.Foursquare.Foursquare
import com.practice.francisco.checkins.Interfaces.CategoriasVenuesInterface
import com.practice.francisco.checkins.R
import com.practice.francisco.checkins.RecyclerViewCategorias.AdaptadorCustom
import com.practice.francisco.checkins.RecyclerViewCategorias.ClickListener
import com.practice.francisco.checkins.RecyclerViewCategorias.LongClickListener

class Categorias : AppCompatActivity() {

    var lista: RecyclerView? = null
    var adaptador: AdaptadorCustom? =null
    var layoutManager: RecyclerView.LayoutManager? = null

    ///Toolbar
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        initRecyclerView()
        initToolbar()

        var fsqr = Foursquare(this, Categorias())
        fsqr.cargarCategorias(object : CategoriasVenuesInterface{
            override fun categoriasVenues(categorias: ArrayList<Category>) {
                Log.d("Categorias", categorias.count().toString())
                implementacionRecyclerView(categorias)
            }
        })
    }

    private fun initRecyclerView(){
        lista = findViewById(R.id.rvCategorias)
        lista?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager
    }

    private fun implementacionRecyclerView(categorias:ArrayList<Category>){
        adaptador = AdaptadorCustom(categorias, object: ClickListener {
            override fun onClick(vista: View, index: Int) {
                //Toast.makeText(applicationContext, lugares.get(index).name, Toast.LENGTH_SHORT).show()
                /*val venueToJson = Gson()
                val venueActualString = venueToJson.toJson(categorias.get(index))
                val intent = Intent(applicationContext, DetallesVenue::class.java)
                intent.putExtra(PantallaPrincipal.VENUE_ACTUAL, venueActualString)
                startActivity(intent)*/
            }
        },object: LongClickListener {
            override fun longClick(vista: View, index: Int) {

            }

        })
        lista?.adapter = adaptador
    }

    fun initToolbar(){
        toolbar = findViewById(R.id.appToolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }
}
