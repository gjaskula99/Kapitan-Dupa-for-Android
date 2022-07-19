package com.example.kapitandupa

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button

class GameActivity : AppCompatActivity() {
    var ready : Boolean = false
    var points : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d("GAME", "Activity started")

        var mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.start()
        Log.d("AUDIO", "Media player started - raw.start")
        Log.d("AUDIO", mediaPlayer.isPlaying.toString())

        val button = this.findViewById<Button>(R.id.rypanie)
        button.setOnClickListener{
            if(ready){
                points += 1
                Log.d("POINTS", "Points set to $points")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")

    }

}