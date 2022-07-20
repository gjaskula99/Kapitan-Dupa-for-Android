package com.example.kapitandupa

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        Log.d("GAME", "Game over started")
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Game over resumed")
        val button : Button = this.findViewById<Button>(R.id.restart)
        var mediaPlayer = MediaPlayer.create(this, R.raw.gameover)
        mediaPlayer.start()
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            mediaPlayer = MediaPlayer.create(this, R.raw.lowscore)
            mediaPlayer.start()
            button.setOnClickListener() {
                Log.d("UI", "Restart button listener called")
            }
        }, 4000)
    }
}