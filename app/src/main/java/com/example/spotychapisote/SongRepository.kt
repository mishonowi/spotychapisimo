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

                escuchasDelDia = obj.optInt("escuchasDelDia", 0),
                dayIndexUltimaEscucha = obj.optInt("dayIndexUltimaEscucha", 0)
            )
            lista.add(cancion)
        }
        if (lista.isEmpty()) {
            val seed = cancionesPorDefecto()
            lista.addAll(seed)
            guardarCanciones(lista)
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

            escuchasDelDia = 0,
            dayIndexUltimaEscucha = 0
        )

        lista.add(0, nueva)
        guardarCanciones(lista)
    }
    private fun cancionesPorDefecto(): List<Song> {
        return listOf(
            Song(UUID.randomUUID().toString(), "Bohemian Rhapsody", "Queen", "https://youtu.be/fJ9rUzIMcZQ", "", 0, 0L, 0, 0),      // Rock
            Song(UUID.randomUUID().toString(), "Enter Sandman", "Metallica", "https://youtu.be/CD-E-LDc384", "", 0, 0L, 0, 0),      // Metal
            Song(UUID.randomUUID().toString(), "Billie Jean", "Michael Jackson", "https://youtu.be/Zi_XLOBDo_Y", "", 0, 0L, 0, 0), // Pop
            Song(UUID.randomUUID().toString(), "Gasolina", "Daddy Yankee", "https://youtu.be/CCF1_jI8Prk", "", 0, 0L, 0, 0),       // Reggaetón
            Song(UUID.randomUUID().toString(), "Smells Like Teen Spirit", "Nirvana", "https://youtu.be/hTWKbfoikeg", "", 0, 0L, 0, 0), // Grunge
            Song(UUID.randomUUID().toString(), "Lose Yourself", "Eminem", "https://youtu.be/_Yhyp-_hX2s", "", 0, 0L, 0, 0),        // Rap / Hip-Hop
            Song(UUID.randomUUID().toString(), "I Gotta Feeling", "Black Eyed Peas", "https://youtu.be/uSD4vsh1zDA", "", 0, 0L, 0, 0), // Dance
            Song(UUID.randomUUID().toString(), "Take Me Home, Country Roads", "John Denver", "https://youtu.be/1vrEljMfXYo", "", 0, 0L, 0, 0), // Country
            Song(UUID.randomUUID().toString(), "No Woman, No Cry", "Bob Marley", "https://youtu.be/IT8XvzIfi4U", "", 0, 0L, 0, 0), // Reggae
            Song(UUID.randomUUID().toString(), "Clocks", "Coldplay", "https://youtu.be/d020hcWA_Wg", "", 0, 0L, 0, 0),             // Alternative

        )
    }

}
