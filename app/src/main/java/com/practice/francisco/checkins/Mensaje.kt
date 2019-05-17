package com.practice.francisco.checkins

import android.content.Context
import android.widget.Toast

class Mensaje {
    companion object {
        fun mensajeSuccess(){

        }
        fun mensaje(context: Context, mensaje: Mensajes){
            var str = ""
            when(mensaje){
                Mensajes.RATIONALE->{
                    str = "Requiero permiso para obtener ubicación"
                }
            }
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }
        fun mensajeError(context: Context, error:Errores){
            var mensaje = ""
            when(error){
                Errores.NO_HAY_RED->{
                    mensaje = "No hay mensaje disponible"
                }
                Errores.HTTP_ERROR->{
                    mensaje = "Hubo un error en la solicitud. Intenta más tarde"
                }
                Errores.NO_HAY_APP_FOURSQUARE->{
                    mensaje = "No tienes instalada la aplicación de Foursquare"
                }
                Errores.ERROR_CONEXION_FSQR->{
                    mensaje = "No se pudo conectar a Foursquare"
                }
                Errores.ERROR_INTERCAMBIO_TOKEN->{
                    mensaje = "Ocurrio un error al hacer el intercambio de token"
                }
                Errores.ERROR_GUARDAR_TOKEN->{
                    mensaje = "Ocurrio un error al guardar el token"
                }
                Errores.ERROR_PERMISO_NEGADO->{
                    mensaje = "La app no tiene persimos para obtner ubicación"
                }
            }
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}