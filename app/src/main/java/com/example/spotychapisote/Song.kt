package com.example.spotychapisote

data class Song(
    val id: String,
    var titulo: String,
    var artista: String,
    var linkYoutube: String,
    var coverUri: String = "",
    var vecesEscuchada: Int = 0,
    var ultimaVezMs: Long = 0L,
    var dayIndexUltimaEscucha: Int = 0,
    var escuchasDelDia: Int = 0
)
