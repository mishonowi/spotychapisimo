package com.example.spotychapisote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotychapisote.databinding.ActivitySongsBinding
import java.util.LinkedHashSet

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

        binding.btnBack.setOnClickListener { finish() }

        // LAST PLAYED inicial
        cargarLastPlayed(listaCompleta)

        // SEARCH con Trie
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s?.toString()?.trim() ?: ""
                aplicarFiltro(texto)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        listaCompleta = repo.obtenerCanciones()
        reconstruirTrie(listaCompleta)

        // refresca lista según búsqueda actual
        val texto = binding.inputSearch.text.toString().trim()
        aplicarFiltro(texto)

        // refresca last played
        cargarLastPlayed(listaCompleta)
    }

    private fun aplicarFiltro(texto: String) {
        if (texto.isEmpty()) {
            adapter.updateSongs(listaCompleta)
            return
        }

        val ids = songTrie.searchPrefix(texto)

        // ✅ quitar repetidos manteniendo orden
        val unicos = LinkedHashSet<String>()
        for (id in ids) unicos.add(id)

        val filtradas: MutableList<Song> = mutableListOf()
        for (id in unicos) {
            val song = mapaIdSong[id]
            if (song != null) filtradas.add(song)
        }

        adapter.updateSongs(filtradas)
    }

    private fun reconstruirTrie(lista: MutableList<Song>) {
        songTrie.init()
        mapaIdSong.clear()

        for (song in lista) {
            mapaIdSong[song.id] = song

            // indexa por titulo y artista
            songTrie.insertWord(song.titulo, song.id)
            songTrie.insertWord(song.artista, song.id)
        }
    }

    private fun cargarLastPlayed(lista: MutableList<Song>) {
        val ordenadas = lista
            .filter { it.ultimaVezMs > 0L }
            .sortedByDescending { it.ultimaVezMs }

        val s1 = ordenadas.getOrNull(0)
        val s2 = ordenadas.getOrNull(1)

        if (s1 == null) {
            binding.textLastPlayed.visibility = View.GONE
            binding.layoutLastPlayed.visibility = View.GONE
            return
        } else {
            binding.textLastPlayed.visibility = View.VISIBLE
            binding.layoutLastPlayed.visibility = View.VISIBLE
        }

        // CARD 1
        binding.cardLast1.visibility = View.VISIBLE
        binding.textLastArtist1.text = s1.artista
        binding.textLastTitle1.text = s1.titulo
        if (s1.coverUri.isNotEmpty()) {
            binding.imgLast1.setImageURI(Uri.parse(s1.coverUri))
        } else {
            binding.imgLast1.setImageResource(R.drawable.image_default)
        }
        binding.cardLast1.setOnClickListener { abrirDetalle(s1.id) }

        // CARD 2
        if (s2 == null) {
            binding.cardLast2.visibility = View.INVISIBLE
        } else {
            binding.cardLast2.visibility = View.VISIBLE
            binding.textLastArtist2.text = s2.artista
            binding.textLastTitle2.text = s2.titulo
            if (s2.coverUri.isNotEmpty()) {
                binding.imgLast2.setImageURI(Uri.parse(s2.coverUri))
            } else {
                binding.imgLast2.setImageResource(R.drawable.image_default)
            }
            binding.cardLast2.setOnClickListener { abrirDetalle(s2.id) }
        }
    }

    private fun abrirDetalle(songId: String) {
        val intent = Intent(this, SongDetailActivity::class.java)
        intent.putExtra("SONG_ID", songId)
        startActivity(intent)
    }
}

