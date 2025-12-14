package com.example.spotychapisote

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotychapisote.databinding.ActivityAddSongBinding


class AddSongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSongBinding
    private val context: Context = this

    private var coverUriString: String = ""

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                coverUriString = uri.toString()
                binding.imageCoverPick.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddSongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBackAdd.setOnClickListener {
            finish()
        }

        binding.cardCoverPick.setOnClickListener {
            seleccionarImagen.launch(arrayOf("image/*"))
        }
        binding.imageCoverPick.setOnClickListener {
            seleccionarImagen.launch(arrayOf("image/*"))
        }

        binding.btnGuardar.setOnClickListener {
            val titulo = binding.inputNombreCancion.text.toString().trim()
            val artista = binding.inputArtista.text.toString().trim()
            val link = binding.inputLink.text.toString().trim()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Agregar titulo", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (artista.isEmpty()) {
                Toast.makeText(this, "Agregar artista", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (link.isEmpty()) {
                Toast.makeText(this, "Agregar link", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if(!esLinkYouTube(link)){
                Toast.makeText(this, "Agregar link de youtube", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val repo = SongRepository(context)
            repo.agregarCancion(
                titulo = titulo,
                artista = artista,
                linkYoutube = link,
                coverUri = coverUriString
            )
            finish()
        }
    }

    fun esLinkYouTube(link: String): Boolean {
        val s = link.trim()

        val uri = runCatching { Uri.parse(s) }.getOrNull() ?: return false
        val host = (uri.host ?: "").lowercase()

        val esDominioYT = host == "youtu.be" ||
                host.endsWith("youtube.com") ||
                host.endsWith("youtube-nocookie.com")
        return esDominioYT
    }

}
