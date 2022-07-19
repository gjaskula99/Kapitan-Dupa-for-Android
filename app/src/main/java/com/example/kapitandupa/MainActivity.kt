package com.example.kapitandupa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.VideoView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("INTRO", "Now playing")
        val intro = findViewById<View>(R.id.video_intro) as VideoView
        intro.setMediaController(null)
        intro.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.intro))
        intro.requestFocus()
        intro.start()

        intro!!.setOnCompletionListener {
            //Toast.makeText(applicationContext, "Video completed",
                //Toast.LENGTH_LONG).show()
            Log.d("INTRO", "Finished. Starting game activity")
            val intent = Intent(this, GameActivity::class.java).apply {  }
            startActivity(intent)
        }
    }
}