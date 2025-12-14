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

        val songId = intent.getStringExtra("SONG_ID") ?: ""
        if (songId.isEmpty()) {
            finish()
            return
        }

        val lista = repo.obtenerCanciones()
        val song = lista.find { it.id == songId }
        if (song == null) {
            finish()
            return
        }

        // UI
        binding.textTitle.text = song.titulo
        binding.textArtist.text = song.artista

        if (song.coverUri.isNotEmpty()) {
            binding.imageCover.setImageURI(Uri.parse(song.coverUri))
        } else {
            binding.imageCover.setImageResource(R.drawable.cover_default)
        }

        // Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Escuchar (abre YouTube)
        binding.btnListen.setOnClickListener {
            if (song.linkYoutube.isNotEmpty()) {
                val intentYoutube = Intent(Intent.ACTION_VIEW, Uri.parse(song.linkYoutube))
                startActivity(intentYoutube)
                song.vecesEscuchada += 1
                song.ultimaVezMs = System.currentTimeMillis()
                repo.guardarCanciones(lista)
            } else {
                Toast.makeText(this, "Video no disponible", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
