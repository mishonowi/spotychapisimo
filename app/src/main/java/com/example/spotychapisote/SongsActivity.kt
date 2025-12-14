package com.example.spotychapisote

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private val songTrie = SongTrie()
    private var listaCompleta: MutableList<Song> = mutableListOf()
    private val mapaIdSong: HashMap<String, Song> = HashMap()

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

        listaCompleta = repo.obtenerCanciones()
        reconstruirTrie(listaCompleta)

        adapter = SongAdapter(listaCompleta) { song ->
            val intent = Intent(this, SongDetailActivity::class.java)
            intent.putExtra("SONG_ID", song.id)
            startActivity(intent)
        }

        binding.recyclerSongs.layoutManager = LinearLayoutManager(this)
        binding.recyclerSongs.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s?.toString()?.trim() ?: ""

                if (texto.isEmpty()) {
                    adapter.updateSongs(listaCompleta)
                    return
                }

                val ids = songTrie.searchPrefix(texto)

                val filtradas: MutableList<Song> = mutableListOf()
                for (id in ids) {
                    val song = mapaIdSong[id]
                    if (song != null) {
                        filtradas.add(song)
                    }
                }

                adapter.updateSongs(filtradas)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        listaCompleta = repo.obtenerCanciones()
        reconstruirTrie(listaCompleta)

        val texto = binding.inputSearch.text.toString().trim()
        if (texto.isEmpty()) {
            adapter.updateSongs(listaCompleta)
        } else {
            val ids = songTrie.searchPrefix(texto)
            val filtradas: MutableList<Song> = mutableListOf()
            for (id in ids) {
                val song = mapaIdSong[id]
                if (song != null) filtradas.add(song)
            }
            adapter.updateSongs(filtradas)
        }
    }

    private fun reconstruirTrie(lista: MutableList<Song>) {
        songTrie.init()
        mapaIdSong.clear()

        for (song in lista) {
            mapaIdSong[song.id] = song

            songTrie.insertWord(song.titulo, song.id)
            songTrie.insertWord(song.artista, song.id)
        }
    }
}
