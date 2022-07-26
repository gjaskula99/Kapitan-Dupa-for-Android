package com.example.kapitandupa

import android.content.Intent
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
        Log.d("GAME", "Game over created")
        val button : Button = this.findViewById<Button>(R.id.restart)
        var mediaPlayer = MediaPlayer.create(this, R.raw.gameover)
        mediaPlayer.start()
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            mediaPlayer = MediaPlayer.create(this, R.raw.lowscore)
            mediaPlayer.start()
        }, 4000)
        mHandler.postDelayed(Runnable {
            button.setOnClickListener() {
                Log.d("UI", "Restart button listener called")
                val intent = Intent(this, GameActivity::class.java).apply {  }
                startActivity(intent)
            }
        }, 12000)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}