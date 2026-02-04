package com.example.kapitandupa

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView

class HighScoreActivity : AppCompatActivity() {
    private var highScoreMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        Log.d("GAME", "High score activity created")

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

        // Display the score
        val scoreValueText: TextView = findViewById(R.id.score_value)
        scoreValueText.text = finalScore.toString()

        // Get the exclamation mark and make it blink
        val exclamationText: TextView = findViewById(R.id.exclamation)
        val blinkAnimator = ObjectAnimator.ofFloat(exclamationText, "alpha", 1f, 0.2f, 1f)
        blinkAnimator.duration = 500 // Each blink cycle takes 500ms
        blinkAnimator.repeatCount = 5 // Blink 6 times total (0-5)
        blinkAnimator.interpolator = AccelerateDecelerateInterpolator()
        blinkAnimator.start()

        // Play highscore audio
        highScoreMediaPlayer = MediaPlayer.create(this, R.raw.highscore)
        highScoreMediaPlayer?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                highScoreMediaPlayer = null
            }
            start()
        }

        // Auto-advance to NameEntryActivity after 8 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, NameEntryActivity::class.java).apply {
                putExtra("FINAL_SCORE", finalScore)
            }
            startActivity(intent)
            finish()
        }, 8000)
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "High score activity paused")
        highScoreMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "High score activity resumed")
        highScoreMediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        highScoreMediaPlayer?.release()
        highScoreMediaPlayer = null
    }
}
