package com.example.spotychapisote

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotychapisote.databinding.ActivitySongsBinding

class SongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongsBinding
    private lateinit var adapter: SongAdapter
    private lateinit var repo: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repo = SongRepository(this)

        val lista = repo.obtenerCanciones()

        adapter = SongAdapter(lista) { song ->
            val intent = Intent(this, SongDetailActivity::class.java)
            intent.putExtra("SONG_ID", song.id)
            startActivity(intent)
        }

        binding.recyclerSongs.layoutManager = LinearLayoutManager(this)
        binding.recyclerSongs.adapter = adapter

        // si tienes botón back en esta pantalla:
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val nuevaLista = repo.obtenerCanciones()
        adapter.updateSongs(nuevaLista)
    }
}
