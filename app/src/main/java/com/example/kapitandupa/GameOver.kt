package com.example.kapitandupa

import android.content.Intent
import android.media.MediaPlayer
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button

class GameOver : AppCompatActivity() {
    private var gameOverMediaPlayer: MediaPlayer? = null
    private var lowScoreMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        Log.d("GAME", "Game over created")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        })

        val button : Button = this.findViewById<Button>(R.id.restart)
        gameOverMediaPlayer = MediaPlayer.create(this, R.raw.gameover)
        gameOverMediaPlayer?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                gameOverMediaPlayer = null
            }
            start()
        }
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed(Runnable {
            lowScoreMediaPlayer = MediaPlayer.create(this, R.raw.lowscore)
            lowScoreMediaPlayer?.apply {
                setOnCompletionListener { mp ->
                    mp.release()
                    lowScoreMediaPlayer = null
                }
                start()
            }
        }, 4000)
        mHandler.postDelayed(Runnable {
            button.setOnClickListener() {
                Log.d("UI", "Restart button listener called")
                val intent = Intent(this, GameActivity::class.java).apply {  }
                startActivity(intent)
            }
        }, 12000)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameOverMediaPlayer?.release()
        gameOverMediaPlayer = null
        lowScoreMediaPlayer?.release()
        lowScoreMediaPlayer = null
    }
}