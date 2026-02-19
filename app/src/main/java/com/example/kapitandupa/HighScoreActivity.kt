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
    private val mainHandler = Handler(Looper.getMainLooper())
    private var autoAdvanceRunnable: Runnable? = null
    private var autoAdvanceDelayMs: Long = AUTO_ADVANCE_DELAY_MS
    private var autoAdvanceDelayRemainingMs: Long = AUTO_ADVANCE_DELAY_MS
    private var autoAdvanceDelayScheduledAtMs: Long = 0L
    private var wasHighScoreAudioPlaying: Boolean = false
    private var finalScore: Int = 0

    private fun scheduleAutoAdvance(delayMs: Long) {
        val runnable = autoAdvanceRunnable ?: return
        mainHandler.removeCallbacks(runnable)
        autoAdvanceDelayMs = delayMs
        autoAdvanceDelayRemainingMs = delayMs
        autoAdvanceDelayScheduledAtMs = android.os.SystemClock.elapsedRealtime()
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun pauseAutoAdvance() {
        val runnable = autoAdvanceRunnable ?: return
        val elapsed = android.os.SystemClock.elapsedRealtime() - autoAdvanceDelayScheduledAtMs
        autoAdvanceDelayRemainingMs = (autoAdvanceDelayMs - elapsed).coerceAtLeast(0L)
        mainHandler.removeCallbacks(runnable)
    }

    private fun resumeAutoAdvance() {
        if (autoAdvanceRunnable != null && autoAdvanceDelayRemainingMs >= 0L) {
            scheduleAutoAdvance(autoAdvanceDelayRemainingMs)
        }
    }

    private fun clearAutoAdvance(resetState: Boolean) {
        autoAdvanceRunnable?.let(mainHandler::removeCallbacks)
        if (resetState) {
            autoAdvanceRunnable = null
            autoAdvanceDelayRemainingMs = AUTO_ADVANCE_DELAY_MS
        }
    }

    companion object {
        private const val AUTO_ADVANCE_DELAY_MS = 8_000L
    }

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

        finalScore = intent.getIntExtra("FINAL_SCORE", 0)

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
        autoAdvanceRunnable = Runnable {
            autoAdvanceRunnable = null
            autoAdvanceDelayRemainingMs = 0L
            if (isFinishing || isDestroyed) {
                return@Runnable
            }
            val intent = Intent(this, NameEntryActivity::class.java).apply {
                putExtra("FINAL_SCORE", finalScore)
            }
            startActivity(intent)
            finish()
        }
        scheduleAutoAdvance(AUTO_ADVANCE_DELAY_MS)
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "High score activity paused")
        wasHighScoreAudioPlaying = highScoreMediaPlayer?.isPlaying == true
        pauseAutoAdvance()
        highScoreMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "High score activity resumed")
        if (wasHighScoreAudioPlaying) {
            highScoreMediaPlayer?.takeIf { !it.isPlaying }?.start()
            wasHighScoreAudioPlaying = false
        }
        resumeAutoAdvance()
    }

    override fun onStop() {
        super.onStop()
        clearAutoAdvance(resetState = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAutoAdvance(resetState = true)
        highScoreMediaPlayer?.release()
        highScoreMediaPlayer = null
    }
}
