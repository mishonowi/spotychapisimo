package com.example.spotychapisote

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SongRepository(private val context: Context) {

    private val nombrePreferencias = "spoty_local"
    private val claveCanciones = "songs_json"

    private fun preferencias() =
        context.getSharedPreferences(nombrePreferencias, Context.MODE_PRIVATE)

    fun obtenerCanciones(): MutableList<Song> {
        val textoJson = preferencias().getString(claveCanciones, "[]") ?: "[]"
        val arregloJson = JSONArray(textoJson)

        val lista = mutableListOf<Song>()
        for (i in 0 until arregloJson.length()) {
            val obj = arregloJson.getJSONObject(i)

            val cancion = Song(
                id = obj.optString("id"),
                titulo = obj.optString("titulo"),
                artista = obj.optString("artista"),
                linkYoutube = obj.optString("linkYoutube"),
                coverUri = obj.optString("coverUri", ""),
                vecesEscuchada = obj.optInt("vecesEscuchada", 0),
                ultimaVezMs = obj.optLong("ultimaVezMs", 0L),

                // ✅ nuevos (wrapped del día)
                escuchasDelDia = obj.optInt("escuchasDelDia", 0),
                dayIndexUltimaEscucha = obj.optInt("dayIndexUltimaEscucha", 0)
            )
            lista.add(cancion)
        }
        return lista
    }

    fun guardarCanciones(lista: List<Song>) {
        val arreglo = JSONArray()
        for (cancion in lista) {
            val obj = JSONObject()
            obj.put("id", cancion.id)
            obj.put("titulo", cancion.titulo)
            obj.put("artista", cancion.artista)
            obj.put("linkYoutube", cancion.linkYoutube)
            obj.put("coverUri", cancion.coverUri)
            obj.put("vecesEscuchada", cancion.vecesEscuchada)
            obj.put("ultimaVezMs", cancion.ultimaVezMs)

            // ✅ nuevos (wrapped del día)
            obj.put("escuchasDelDia", cancion.escuchasDelDia)
            obj.put("dayIndexUltimaEscucha", cancion.dayIndexUltimaEscucha)

            arreglo.put(obj)
        }

        preferencias().edit()
            .putString(claveCanciones, arreglo.toString())
            .apply()
    }

    fun agregarCancion(titulo: String, artista: String, linkYoutube: String, coverUri: String) {
        val lista = obtenerCanciones()

        val nueva = Song(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            artista = artista,
            linkYoutube = linkYoutube,
            coverUri = coverUri,
            vecesEscuchada = 0,
            ultimaVezMs = 0L,

            // ✅ nuevos
            escuchasDelDia = 0,
            dayIndexUltimaEscucha = 0
        )

        lista.add(0, nueva)
        guardarCanciones(lista)
    }
}
