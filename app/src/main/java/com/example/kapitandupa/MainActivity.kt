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
    var stopPosition : Int = 0

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

    override fun onPause() {
        Log.d("INTRO", "Paused")
        super.onPause()
        val intro = findViewById<View>(R.id.video_intro) as VideoView
        stopPosition = intro.getCurrentPosition();
        intro.pause()
    }

    override fun onResume() {
        Log.d("INTRO", "Resumed")
        super.onResume()
        val intro = findViewById<View>(R.id.video_intro) as VideoView
        intro.seekTo(stopPosition)
        intro.start()
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}