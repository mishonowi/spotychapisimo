package com.example.spotychapisote

import android.content.Intent
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // 1. Mueve la variable aquí para que sea una propiedad de la clase
    private var isVideoFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // El resto del código debería ir preferiblemente AQUÍ,
        // fuera del listener de ViewCompat, a menos que realmente dependa de los insets.

        // Mover la inicialización del VideoView aquí
        val videoView = findViewById<VideoView>(R.id.main)
        videoView.setVideoPath("android.resource://" + packageName + "/" + R.raw.videoo)

        // 1. When video finishes, set flag to true
        videoView.setOnCompletionListener {
            isVideoFinished = true
        }

        // 2. On click, check flag and move if true
        videoView.setOnClickListener {
            if (isVideoFinished) {
                // Usa this@MainActivity para el contexto
                val intent = Intent(this@MainActivity, Menu::class.java)
                startActivity(intent)
                finish()
            }
        }
        videoView.start()

        // Deja el ViewCompat solo para la parte de Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}