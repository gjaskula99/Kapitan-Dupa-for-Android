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

        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        val isHighScore = LeaderboardManager.isHighScore(this, finalScore)

        val restartButton: Button = findViewById(R.id.restart)
        val viewLeaderboardButton: Button = findViewById(R.id.view_leaderboard)

        gameOverMediaPlayer = MediaPlayer.create(this, R.raw.gameover)
        gameOverMediaPlayer?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                gameOverMediaPlayer = null

                // Route based on high score status
                if (isHighScore) {
                    val intent = Intent(this@GameOver, HighScoreActivity::class.java).apply {
                        putExtra("FINAL_SCORE", finalScore)
                    }
                    startActivity(intent)
                    finish()
                }
            }
            start()
        }

        if (!isHighScore) {
            // Low score path: play lowscore audio and enable buttons
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.postDelayed(Runnable {
                lowScoreMediaPlayer = MediaPlayer.create(this, R.raw.lowscore)
                lowScoreMediaPlayer?.apply {
                    setOnCompletionListener { mp ->
                        mp.release()
                        lowScoreMediaPlayer = null

                        // Show buttons after audio completes
                        restartButton.visibility = Button.VISIBLE
                        restartButton.setOnClickListener {
                            Log.d("UI", "Restart button listener called")
                            val intent = Intent(this@GameOver, GameActivity::class.java).apply { }
                            startActivity(intent)
                        }

                        viewLeaderboardButton.visibility = Button.VISIBLE
                        viewLeaderboardButton.setOnClickListener {
                            Log.d("UI", "View leaderboard button listener called")
                            val intent = Intent(this@GameOver, LeaderboardActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    start()
                }
            }, 4000)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "Game over activity paused")
        gameOverMediaPlayer?.pause()
        lowScoreMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Game over activity resumed")
        // Resume audio if it was playing
        gameOverMediaPlayer?.takeIf { !it.isPlaying }?.start()
        lowScoreMediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameOverMediaPlayer?.release()
        gameOverMediaPlayer = null
        lowScoreMediaPlayer?.release()
        lowScoreMediaPlayer = null
    }
}