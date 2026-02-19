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
    private val mainHandler = Handler(Looper.getMainLooper())

    private var lowScoreStartRunnable: Runnable? = null
    private var lowScoreDelayMs: Long = LOW_SCORE_DELAY_MS
    private var lowScoreDelayRemainingMs: Long = LOW_SCORE_DELAY_MS
    private var lowScoreDelayScheduledAtMs: Long = 0L
    private var wasGameOverAudioPlaying: Boolean = false
    private var wasLowScoreAudioPlaying: Boolean = false
    private var isLowScoreFlow: Boolean = false

    private fun scheduleLowScoreFlow(delayMs: Long) {
        val runnable = lowScoreStartRunnable ?: return
        mainHandler.removeCallbacks(runnable)
        lowScoreDelayMs = delayMs
        lowScoreDelayRemainingMs = delayMs
        lowScoreDelayScheduledAtMs = android.os.SystemClock.elapsedRealtime()
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun pauseLowScoreFlow() {
        val runnable = lowScoreStartRunnable ?: return
        val elapsed = android.os.SystemClock.elapsedRealtime() - lowScoreDelayScheduledAtMs
        lowScoreDelayRemainingMs = (lowScoreDelayMs - elapsed).coerceAtLeast(0L)
        mainHandler.removeCallbacks(runnable)
    }

    private fun resumeLowScoreFlow() {
        if (isLowScoreFlow && lowScoreStartRunnable != null && lowScoreDelayRemainingMs >= 0L) {
            scheduleLowScoreFlow(lowScoreDelayRemainingMs)
        }
    }

    private fun clearLowScoreFlow(resetState: Boolean) {
        lowScoreStartRunnable?.let(mainHandler::removeCallbacks)
        if (resetState) {
            lowScoreStartRunnable = null
            lowScoreDelayRemainingMs = LOW_SCORE_DELAY_MS
        }
    }

    companion object {
        private const val LOW_SCORE_DELAY_MS = 4_000L
    }

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

        isLowScoreFlow = !isHighScore

        if (isLowScoreFlow) {
            // Low score path: play lowscore audio and enable buttons
            lowScoreStartRunnable = Runnable {
                lowScoreStartRunnable = null
                lowScoreDelayRemainingMs = 0L
                if (isFinishing || isDestroyed) {
                    return@Runnable
                }
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
            }
            scheduleLowScoreFlow(LOW_SCORE_DELAY_MS)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "Game over activity paused")
        wasGameOverAudioPlaying = gameOverMediaPlayer?.isPlaying == true
        wasLowScoreAudioPlaying = lowScoreMediaPlayer?.isPlaying == true
        pauseLowScoreFlow()
        gameOverMediaPlayer?.pause()
        lowScoreMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Game over activity resumed")
        if (wasGameOverAudioPlaying) {
            gameOverMediaPlayer?.takeIf { !it.isPlaying }?.start()
            wasGameOverAudioPlaying = false
        }
        if (wasLowScoreAudioPlaying) {
            lowScoreMediaPlayer?.takeIf { !it.isPlaying }?.start()
            wasLowScoreAudioPlaying = false
        }
        resumeLowScoreFlow()
    }

    override fun onStop() {
        super.onStop()
        clearLowScoreFlow(resetState = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearLowScoreFlow(resetState = true)
        gameOverMediaPlayer?.release()
        gameOverMediaPlayer = null
        lowScoreMediaPlayer?.release()
        lowScoreMediaPlayer = null
    }
}
