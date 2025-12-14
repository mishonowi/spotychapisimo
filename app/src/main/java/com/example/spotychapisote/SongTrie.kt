package com.example.spotychapisote

class SongTrie {

    private class Node {
        val hijos: Array<Node?> = arrayOfNulls(27)
        var contador: Int = 0

        val idsCanciones: MutableList<String> = mutableListOf()
    }

    private val raiz = Node()

    private fun indice(c: Char): Int {
        val ch = c.lowercaseChar()
        if (ch in 'a'..'z') return ch.code - 'a'.code
        return 26
    }

    private fun normalizar(texto: String): String {
        return texto.trim().lowercase()
    }

    fun insertarClave(clave: String, idCancion: String) {
        val palabra = normalizar(clave)
        if (palabra.isEmpty()) return

        var nodoActual = raiz
        for (i in palabra.indices) {
            val pos = indice(palabra[i])
            if (nodoActual.hijos[pos] == null) {
                nodoActual.hijos[pos] = Node()
            }
            nodoActual = nodoActual.hijos[pos]!!

            nodoActual.contador++

            if (!nodoActual.idsCanciones.contains(idCancion)) {
                nodoActual.idsCanciones.add(idCancion)
            }
        }
    }

    fun insertarCancion(song: Song) {
        insertarClave(song.titulo, song.id)
        insertarClave(song.artista, song.id)
    }

    fun contarPrefijo(prefijo: String): Int {
        val p = normalizar(prefijo)
        if (p.isEmpty()) return 0

        var nodoActual = raiz
        for (i in p.indices) {
            val pos = indice(p[i])
            val sig = nodoActual.hijos[pos] ?: return 0
            nodoActual = sig
        }
        return nodoActual.contador
    }

    fun buscarIdsPorPrefijo(prefijo: String): List<String> {
        val p = normalizar(prefijo)
        if (p.isEmpty()) return emptyList()

        var nodoActual = raiz
        for (i in p.indices) {
            val pos = indice(p[i])
            val sig = nodoActual.hijos[pos] ?: return emptyList()
            nodoActual = sig
        }
        return nodoActual.idsCanciones
    }
}
