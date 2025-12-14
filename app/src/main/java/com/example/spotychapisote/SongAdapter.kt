package com.example.spotychapisote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spotychapisote.databinding.ItemSongBinding

class SongAdapter(
    private var songs: MutableList<Song>,
    private val onClickItem: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.binding.textTituloCancion.text = song.titulo
        holder.binding.textNombreArtista.text = song.artista

        holder.binding.cardSongItem.setOnClickListener {
            onClickItem(song)
        }
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: MutableList<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}

