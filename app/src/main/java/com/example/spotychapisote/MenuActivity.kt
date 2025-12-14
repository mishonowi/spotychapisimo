package com.example.spotychapisote
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotychapisote.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    val context: Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnMisCanciones.setOnClickListener {
            val intentSongsActivity = Intent(context, SongsActivity::class.java)

            startActivity(intentSongsActivity)
        }
        binding.btnEstadisticas.setOnClickListener {
            val intentStatsActivity = Intent(context, StatsActivity::class.java)

            startActivity(intentStatsActivity)
        }
        binding.btnAgregarCancion.setOnClickListener {
            val intentAddSongActivity = Intent(context, AddSongActivity::class.java)

            startActivity(intentAddSongActivity)
        }

    }


}