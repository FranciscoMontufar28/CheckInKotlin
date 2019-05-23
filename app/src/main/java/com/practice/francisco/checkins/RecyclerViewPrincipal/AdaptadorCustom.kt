package com.practice.francisco.checkins.RecyclerViewPrincipal

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.practice.francisco.checkins.FoursquareAPIRequestVenues
import com.practice.francisco.checkins.R
import kotlinx.android.synthetic.main.template_venues.view.*

class AdaptadorCustom(items:ArrayList<FoursquareAPIRequestVenues.Venue>, var listener: ClickListener, var longClickListener: LongClickListener):RecyclerView.Adapter<AdaptadorCustom.ViewHolder>(){
    var items:ArrayList<FoursquareAPIRequestVenues.Venue>? = null
    var multiSeleccion = false
    var itemsSeleccionados:ArrayList<Int>? = null
    var viewHolder:ViewHolder? = null
    init {
        this.items = items
        itemsSeleccionados = ArrayList()
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdaptadorCustom.ViewHolder {
        val vista = LayoutInflater.from(p0.context).inflate(R.layout.template_venues,p0,false)
        viewHolder = ViewHolder(vista, listener, longClickListener)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val items = items?.get(position)
        holder.nombre?.text = items?.name!!

        if (itemsSeleccionados?.contains(position)!!){
            holder.vista.setBackgroundColor(Color.LTGRAY)
        }else{
            holder.vista.setBackgroundColor(Color.WHITE)
        }
    }

    fun iniciarActionMode(){
        multiSeleccion = true
    }

    fun destruirActionMode(){
        multiSeleccion = false
        itemsSeleccionados?.clear()
        notifyDataSetChanged()
    }

    fun terminarActionMode(){
        //eliminar elementos seleccionados
        for(item in itemsSeleccionados!!){
            itemsSeleccionados?.remove(item)
        }
        multiSeleccion = false
        notifyDataSetChanged()
    }

    fun seleccionarItem(index:Int){
        if (multiSeleccion){
            if (itemsSeleccionados?.contains(index)!!){
                itemsSeleccionados?.remove(index)
            }else{
                itemsSeleccionados?.add(index)
            }
            notifyDataSetChanged()
        }
    }

    fun obtenerNumeroElementosSeleccionados():Int{
        return itemsSeleccionados?.count()!!
    }

    fun eliminarSeleccionados(){
        if (itemsSeleccionados?.count()!!>0){
            var itemsEliminados = ArrayList<FoursquareAPIRequestVenues.Venue>()
            for (index in itemsSeleccionados!!){
                itemsEliminados.add(items?.get(index)!!)
            }
            items?.removeAll(itemsEliminados)
            itemsSeleccionados?.clear()
        }
    }

    class ViewHolder(vista:View, listener: ClickListener, longClickListener: LongClickListener):RecyclerView.ViewHolder(vista), View.OnClickListener, View.OnLongClickListener{
        var vista = vista
        var nombre:TextView? = null

        var listener:ClickListener? = null
        var longListener:LongClickListener? = null
        init {
            nombre = vista.tvNombre
            this.listener =  listener
            this.longListener = longClickListener
            vista.setOnClickListener(this)
            vista.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onClick(v!!, adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            this.longListener?.longClick(v!!,adapterPosition)
            return true
        }
    }
}