package com.example.spotychapisote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotychapisote.databinding.ActivitySongDetailBinding

class SongDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongDetailBinding
    private lateinit var repo: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySongDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repo = SongRepository(this)

        val songId = intent.getStringExtra("SONG_ID")
        if (songId.isNullOrEmpty()) {
            finish()
            return
        }

        val lista = repo.obtenerCanciones()
        val song = lista.find { it.id == songId }
        if (song == null) {
            finish()
            return
        }

        binding.textTitle.text = song.titulo
        binding.textArtist.text = song.artista

        if (song.coverUri.isNotEmpty()) {
            binding.imageCover.setImageURI(Uri.parse(song.coverUri))
        } else {
            binding.imageCover.setImageResource(R.drawable.image_default)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnListen.setOnClickListener {

            val link = song.linkYoutube.trim()
            if (link.isEmpty()) {
                Toast.makeText(this, "Video no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ahoraMs = System.currentTimeMillis()

            val dayMs = 86_400_000L
            val hoyIndex = (ahoraMs / dayMs).toInt()

            if (song.dayIndexUltimaEscucha != hoyIndex) {
                song.dayIndexUltimaEscucha = hoyIndex
                song.escuchasDelDia = 0
            }

            song.vecesEscuchada += 1
            song.escuchasDelDia += 1
            song.ultimaVezMs = ahoraMs

            repo.guardarCanciones(lista)

            val intentYoutube = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intentYoutube)
        }
    }
}

