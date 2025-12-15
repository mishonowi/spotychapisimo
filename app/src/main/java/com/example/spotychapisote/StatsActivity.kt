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

        // ===== índice del día actual =====
        val hoyIndex = (System.currentTimeMillis() / 86_400_000L).toInt()

        // ===== BIT =====
        val bit = Bit(lista.size)

        var huboCambios = false

        // ===== llenar BIT con escuchas del día =====
        for (i in lista.indices) {
            val song = lista[i]

            if (song.dayIndexUltimaEscucha != hoyIndex) {
                song.dayIndexUltimaEscucha = hoyIndex
                song.escuchasDelDia = 0
                huboCambios = true
            }

            bit.update(i + 1, song.escuchasDelDia)
        }

        if (huboCambios) {
            repo.guardarCanciones(lista)
        }

        // ===== TOP 1 canción =====
        var topPos = 1
        var topValue = bit.valueAt(1)

        for (i in 2..lista.size) {
            val v = bit.valueAt(i)
            if (v > topValue) {
                topValue = v
                topPos = i
            }
        }

        val topSong = lista[topPos - 1]

        binding.textTopSongTitle.text = topSong.titulo
        binding.textTopArtistSmall.text = topSong.artista

        if (topSong.coverUri.isNotEmpty()) {
            binding.imageTopCover.setImageURI(Uri.parse(topSong.coverUri))
        } else {
            binding.imageTopCover.setImageResource(R.drawable.image_default)
        }

        // ===== TOP 3 canciones =====
        val topCanciones = lista.mapIndexed { index, song ->
            Pair(song, bit.valueAt(index + 1))
        }
            .sortedByDescending { it.second }
            .take(3)

        binding.textSong1.text = topCanciones.getOrNull(0)?.first?.titulo ?: ""
        binding.textSong2.text = topCanciones.getOrNull(1)?.first?.titulo ?: ""
        binding.textSong3.text = topCanciones.getOrNull(2)?.first?.titulo ?: ""

        // ===== TOP 3 artistas =====
        val mapaArtistas = mutableMapOf<String, Int>()

        for (i in lista.indices) {
            val plays = bit.valueAt(i + 1)
            val artista = lista[i].artista
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
