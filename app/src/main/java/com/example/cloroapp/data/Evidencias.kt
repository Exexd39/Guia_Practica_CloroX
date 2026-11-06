package com.example.cloroapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

// Fotos de evidencia guardadas en el almacenamiento INTERNO de la app:
// son privadas (ninguna otra app puede leerlas) y no requieren permisos.
object Evidencias {

    private fun carpeta(context: Context, idPunto: String): File =
        File(context.filesDir, "evidencias/$idPunto").apply { mkdirs() }

    fun guardar(context: Context, idPunto: String, bitmap: Bitmap): File {
        val archivo = File(carpeta(context, idPunto), "${System.currentTimeMillis()}.jpg")
        FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        return archivo
    }

    fun listar(context: Context, idPunto: String): List<File> =
        carpeta(context, idPunto).listFiles()
            ?.filter { it.extension == "jpg" }
            ?.sortedByDescending { it.name }
            ?: emptyList()

    fun eliminar(archivo: File): Boolean = archivo.delete()

    // Lee una imagen de la galeria reduciendo su tamaño para no agotar la memoria
    fun desdeUri(context: Context, uri: Uri, maxLado: Int = 1024): Bitmap? = try {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, medidas)
        }
        val opciones = BitmapFactory.Options().apply {
            inSampleSize = muestreo(medidas.outWidth, medidas.outHeight, maxLado)
        }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        }
    } catch (e: Exception) {
        null
    }

    // Version pequeña de una foto guardada, para mostrarla en la lista
    fun miniatura(archivo: File, maxLado: Int = 256): Bitmap? {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivo.absolutePath, medidas)
        val opciones = BitmapFactory.Options().apply {
            inSampleSize = muestreo(medidas.outWidth, medidas.outHeight, maxLado)
        }
        return BitmapFactory.decodeFile(archivo.absolutePath, opciones)
    }

    private fun muestreo(ancho: Int, alto: Int, maxLado: Int): Int {
        var factor = 1
        var mayor = maxOf(ancho, alto)
        while (mayor / 2 >= maxLado) {
            mayor /= 2
            factor *= 2
        }
        return factor
    }
}