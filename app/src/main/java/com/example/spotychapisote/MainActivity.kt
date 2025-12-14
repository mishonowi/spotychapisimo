package com.example.spotychapisote
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spotychapisote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var isVideoFinished = false
    private lateinit var binding: ActivityMainBinding

    val context: Context =this

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val videoView = findViewById<VideoView>(R.id.main)
        videoView.setVideoPath("android.resource://" + packageName + "/" + R.raw.videoo2)

        videoView.setOnCompletionListener {
            isVideoFinished = true
        }

        videoView.setOnClickListener {
            if (isVideoFinished) {
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        videoView.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.main.setOnClickListener {
            val intentMenuActivity = Intent(context, MenuActivity::class.java)

            startActivity(intentMenuActivity)
        }
    }
}