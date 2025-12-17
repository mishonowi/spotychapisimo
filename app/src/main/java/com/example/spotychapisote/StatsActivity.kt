package com.example.spotychapisote

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotychapisote.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private lateinit var repo: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repo = SongRepository(this)

        binding.btnBackStats.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarStatsDelDia()
    }

    private fun cargarStatsDelDia() {
        val lista = repo.obtenerCanciones()
        if (lista.isEmpty()) return

        val hoyIndex = (System.currentTimeMillis() / 86_400_000L).toInt()
        var huboCambios = false
        for (song in lista) {
            if (song.dayIndexUltimaEscucha != hoyIndex) {
                song.dayIndexUltimaEscucha = hoyIndex
                song.escuchasDelDia = 0
                huboCambios = true
            }
        }
        if (huboCambios) {
            repo.guardarCanciones(lista)
        }


        val cancionesEscuchadas = lista.filter { it.escuchasDelDia > 0 }

        if (cancionesEscuchadas.isEmpty()) {
            binding.textTopSongTitle.text = "No hay reproducciones hoy"
            binding.textTopArtistSmall.text = ""
            binding.imageTopCover.setImageResource(R.drawable.image_default)
            binding.textSong1.text = ""
            binding.textSong2.text = ""
            binding.textSong3.text = ""
            binding.textArtist1.text = ""
            binding.textArtist2.text = ""
            binding.textArtist3.text = ""
            return
        }

        val playsArr = IntArray(cancionesEscuchadas.size)
        for (i in cancionesEscuchadas.indices) {
            playsArr[i] = cancionesEscuchadas[i].escuchasDelDia
        }

        val segmentTree = SegmentTree(playsArr)
        segmentTree.init(0, playsArr.size - 1, 0)


        var topPos = 0
        var topValue = segmentTree.query(0, playsArr.size - 1, 0, 0, 0).max
        for (i in 1 until cancionesEscuchadas.size) {
            val v = segmentTree.query(0, playsArr.size - 1, 0, i, i).max
            if (v > topValue) {
                topValue = v
                topPos = i
            }
        }

        val topSong = cancionesEscuchadas[topPos]
        binding.textTopSongTitle.text = topSong.titulo
        binding.textTopArtistSmall.text = topSong.artista
        if (topSong.coverUri.isNotEmpty()) {
            binding.imageTopCover.setImageURI(Uri.parse(topSong.coverUri))
        } else {
            binding.imageTopCover.setImageResource(R.drawable.image_default)
        }

        val topCanciones = cancionesEscuchadas.mapIndexed { index, song ->
            val plays = segmentTree.query(0, playsArr.size - 1, 0, index, index).max
            Pair(song, plays)
        }
            .sortedByDescending { it.second }
            .take(3)

        binding.textSong1.text = topCanciones.getOrNull(0)?.first?.titulo ?: ""
        binding.textSong2.text = topCanciones.getOrNull(1)?.first?.titulo ?: ""
        binding.textSong3.text = topCanciones.getOrNull(2)?.first?.titulo ?: ""

        val mapaArtistas = mutableMapOf<String, Int>()
        for (i in cancionesEscuchadas.indices) {
            val plays = segmentTree.query(0, playsArr.size - 1, 0, i, i).max
            val artista = cancionesEscuchadas[i].artista
            mapaArtistas[artista] = (mapaArtistas[artista] ?: 0) + plays
        }

        val topArtistas = mapaArtistas.entries
            .sortedByDescending { it.value }
            .take(3)

        binding.textArtist1.text = topArtistas.getOrNull(0)?.key ?: ""
        binding.textArtist2.text = topArtistas.getOrNull(1)?.key ?: ""
        binding.textArtist3.text = topArtistas.getOrNull(2)?.key ?: ""
    }
}