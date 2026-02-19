package com.example.kapitandupa

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.*

data class AudioFile(
    val resource: Int,
    val durationSeconds: Double
)

class GameActivity : AppCompatActivity() {
    var ready : Boolean = false
    var points : Int = 0
    private var startMediaPlayer: MediaPlayer? = null
    private var currentLoopMediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var shootButton: Button
    private lateinit var charactersImage: ImageView
    private lateinit var scoreValueText: TextView
    private lateinit var roundText: TextView
    private lateinit var stageIndicators: List<ImageView>

    private var startGameRunnable: Runnable? = null
    private var nextStageRunnable: Runnable? = null
    private var characterResetRunnable: Runnable? = null

    private var startDelayMs: Long = START_DELAY_MS
    private var startDelayRemainingMs: Long = START_DELAY_MS
    private var startDelayScheduledAtMs: Long = 0L

    private var nextStageDelayMs: Long = 0L
    private var nextStageDelayRemainingMs: Long = 0L
    private var nextStageDelayScheduledAtMs: Long = 0L

    // All 13 audio files with their durations
    private val audioFiles = arrayOf(
        AudioFile(R.raw.rypanie_obrotowa, 4.0),
        AudioFile(R.raw.rypanie_nieczuje, 4.0),
        AudioFile(R.raw.rypanie_nie, 5.0),
        AudioFile(R.raw.rypanie_dopalacze, 6.0),
        AudioFile(R.raw.rypanie_kawalerze, 6.0),
        AudioFile(R.raw.rypanie_jakbabe, 6.0),
        AudioFile(R.raw.rypanie_laser, 9.0),
        AudioFile(R.raw.rypanie_maaaa, 9.0),
        AudioFile(R.raw.rypanie_maaaa2, 10.0),
        AudioFile(R.raw.rypanie_torpedy, 10.0),
        AudioFile(R.raw.rypanie_piana, 11.0),
        AudioFile(R.raw.rypanie_trututututu, 12.0),
        AudioFile(R.raw.rypanie_kolba, 13.0)
    )

    private var lastAudioIndex: Int = -1
    private var cumulativeThreshold: Int = 0
    private var currentAudio: AudioFile? = null

    private fun selectNextAudio(): AudioFile {
        var randomIndex: Int
        do {
            randomIndex = Random().nextInt(audioFiles.size)
        } while (randomIndex == lastAudioIndex && audioFiles.size > 1)

        lastAudioIndex = randomIndex
        return audioFiles[randomIndex]
    }

    private fun getStageDurationMs(audio: AudioFile): Long {
        // Add 1 second margin to audio duration
        return ((audio.durationSeconds + 1.0) * 1000).toLong()
    }

    private fun scheduleStartGame(delayMs: Long) {
        if (ready || isFinishing || !playing) {
            return
        }

        if (startGameRunnable == null) {
            startGameRunnable = Runnable {
                startGameRunnable = null
                startDelayRemainingMs = 0L
                if (isFinishing || isPaused || !playing) {
                    return@Runnable
                }
                ready = true
                shootButton.isEnabled = true
                shootButton.alpha = 1.0f
                Log.d("GAME", "Ready set to $ready")
                timer()
            }
        }

        val runnable = startGameRunnable ?: return
        mainHandler.removeCallbacks(runnable)
        startDelayMs = delayMs
        startDelayRemainingMs = delayMs
        startDelayScheduledAtMs = SystemClock.elapsedRealtime()
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun pauseStartGameCountdown() {
        val runnable = startGameRunnable ?: return
        val elapsed = SystemClock.elapsedRealtime() - startDelayScheduledAtMs
        startDelayRemainingMs = (startDelayMs - elapsed).coerceAtLeast(0L)
        mainHandler.removeCallbacks(runnable)
    }

    private fun scheduleNextStage(delayMs: Long) {
        nextStageRunnable?.let(mainHandler::removeCallbacks)
        val runnable = Runnable {
            nextStageRunnable = null
            nextStageDelayRemainingMs = 0L
            if (isFinishing || isPaused || !playing) {
                return@Runnable
            }
            Log.d("GAME", "Playing is $playing")
            timer()
        }
        nextStageRunnable = runnable
        nextStageDelayMs = delayMs
        nextStageDelayRemainingMs = delayMs
        nextStageDelayScheduledAtMs = SystemClock.elapsedRealtime()
        mainHandler.postDelayed(runnable, delayMs)
    }

    private fun pauseNextStageCountdown() {
        val runnable = nextStageRunnable ?: return
        val elapsed = SystemClock.elapsedRealtime() - nextStageDelayScheduledAtMs
        nextStageDelayRemainingMs = (nextStageDelayMs - elapsed).coerceAtLeast(0L)
        mainHandler.removeCallbacks(runnable)
    }

    private fun resumePendingCountdowns() {
        if (!ready && startGameRunnable != null && startDelayRemainingMs >= 0L) {
            scheduleStartGame(startDelayRemainingMs)
        }
        if (ready && nextStageRunnable != null && nextStageDelayRemainingMs >= 0L) {
            val runnable = nextStageRunnable ?: return
            nextStageDelayMs = nextStageDelayRemainingMs
            nextStageDelayScheduledAtMs = SystemClock.elapsedRealtime()
            mainHandler.postDelayed(runnable, nextStageDelayRemainingMs)
        }
    }

    private fun removeAllCallbacks(resetState: Boolean) {
        startGameRunnable?.let(mainHandler::removeCallbacks)
        nextStageRunnable?.let(mainHandler::removeCallbacks)
        characterResetRunnable?.let(mainHandler::removeCallbacks)
        if (resetState) {
            startGameRunnable = null
            nextStageRunnable = null
            characterResetRunnable = null
            startDelayRemainingMs = START_DELAY_MS
            nextStageDelayRemainingMs = 0L
        }
    }

    companion object {
        private const val START_DELAY_MS = 10_000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d("GAME", "Activity created")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        })

        shootButton = this.findViewById(R.id.rypanie)
        charactersImage = this.findViewById(R.id.characters)
        scoreValueText = this.findViewById(R.id.score)
        roundText = this.findViewById(R.id.roundText)
        stageIndicators = listOf(
            this.findViewById(R.id.fiut1),
            this.findViewById(R.id.fiut2),
            this.findViewById(R.id.fiut3),
            this.findViewById(R.id.fiut4),
            this.findViewById(R.id.fiut5),
            this.findViewById(R.id.fiut6),
            this.findViewById(R.id.fiut7),
            this.findViewById(R.id.fiut8),
            this.findViewById(R.id.fiut9),
            this.findViewById(R.id.fiut10)
        )

        // Disable button initially (greyed out)
        shootButton.isEnabled = false
        shootButton.alpha = 0.5f
        shootButton.setOnClickListener{
            if(ready){
                ryp()
            }
        }
        val scoreTXT = this.findViewById<TextView>(R.id.scoreTXT)
        scoreTXT.setText("SCORE")
        scoreValueText.setText("0")

        startMediaPlayer = MediaPlayer.create(this, R.raw.start)
        startMediaPlayer?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                startMediaPlayer = null
            }
            start()
        }
        points = 0
        stage = 0 //0 for normal start, 10 for game over debugging
        ready = false
        playing = true
        toGameOver = false
        cumulativeThreshold = 0
        startDelayRemainingMs = START_DELAY_MS

        stageIndicators.forEach { it.setImageResource(R.drawable.lotos_1) }

        scheduleStartGame(START_DELAY_MS)
    }

    fun ryp() {
        Log.d("GAME", "Rypanie karabinem")
        points += stage
        scoreValueText.setText(points.toString())
        Log.d("GAME", "Points set to $points")
        charactersImage.setImageResource(R.drawable.dupa_merge_2)
        characterResetRunnable?.let(mainHandler::removeCallbacks)
        characterResetRunnable = Runnable {
            charactersImage.setImageResource(R.drawable.dupa_merge_1)
            characterResetRunnable = null
        }
        mainHandler.postDelayed(characterResetRunnable!!, 50)
    }

    var stage : Int = 0
    var playing : Boolean = true
    var toGameOver: Boolean = false
    private var isPaused: Boolean = false
    private var wasAudioPlaying: Boolean = false

    fun gameover() {
        playing = false
        removeAllCallbacks(resetState = true)
        val intent = Intent(this, GameOver::class.java).apply {
            putExtra("FINAL_SCORE", points)
        }
        startActivity(intent)
    }

    fun timer() {
        // Increment stage
        stage += 1
        Log.d("GAME", "Stage is $stage, Points: $points")

        // Update round display
        roundText.text = "$stage/SEK"

        // Check if game should end after stage 10
        if (stage > 10) {
            playing = false
            Log.d("GAME", "Stage 10 completed, ending game")
            gameover()
            return
        }

        // Select random audio file (excluding previous)
        currentAudio = selectNextAudio()
        val audio = currentAudio!!
        val stageDuration = audio.durationSeconds + 1.0 // Add 1 second margin

        Log.d("GAME", "Stage $stage: Selected audio with duration ${audio.durationSeconds}s")

        // Calculate required points for this stage
        // Required hits: stage * duration (e.g., stage 1 = 1 hit/sec, stage 2 = 2 hits/sec)
        val requiredHits = (stage * stageDuration).toInt()
        // Points per hit in this stage = stage number
        val pointsThisStage = requiredHits * stage
        // Update cumulative threshold
        val previousThreshold = cumulativeThreshold
        cumulativeThreshold += pointsThisStage

        Log.d("GAME", "Stage $stage: Required $requiredHits hits, $pointsThisStage points this stage, cumulative threshold: $cumulativeThreshold")

        // Update fiut visual indicator for current stage
        if (stage in 1..stageIndicators.size) {
            stageIndicators[stage - 1].setImageResource(R.drawable.lotos_2)
        }

        // Check if player met the previous stage's threshold
        if (stage > 1 && points < previousThreshold) {
            Log.d("GAME", "Failed stage ${stage - 1}: $points < $previousThreshold")
            toGameOver = true
        }

        if (toGameOver) {
            currentLoopMediaPlayer?.release()
            currentLoopMediaPlayer = null
            gameover()
            return
        }

        // Release previous MediaPlayer before creating new one
        currentLoopMediaPlayer?.release()
        currentLoopMediaPlayer = null

        // Create MediaPlayer for this stage's randomly selected audio
        val mediaPlayer = MediaPlayer.create(this, audio.resource)
        currentLoopMediaPlayer = mediaPlayer

        // Calculate delay for this stage (audio duration + 1 second margin)
        val delay = getStageDurationMs(audio)
        Log.d("GAME", "Stage $stage delay: ${delay}ms")

        Log.d("GAME", "Game over is $toGameOver")

        scheduleNextStage(delay)

        // Start audio or trigger game over (only if not paused)
        if (!isPaused) {
            currentLoopMediaPlayer?.start()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")
        isPaused = false

        // Resume audio if it was playing before pause
        if (wasAudioPlaying) {
            startMediaPlayer?.takeIf { !it.isPlaying }?.start()
            currentLoopMediaPlayer?.takeIf { !it.isPlaying }?.start()
            wasAudioPlaying = false
        }
        resumePendingCountdowns()
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "Game activity paused")
        isPaused = true

        // Track if audio was playing and pause it
        wasAudioPlaying = (startMediaPlayer?.isPlaying == true) ||
                          (currentLoopMediaPlayer?.isPlaying == true)

        pauseStartGameCountdown()
        pauseNextStageCountdown()
        characterResetRunnable?.let(mainHandler::removeCallbacks)

        startMediaPlayer?.pause()
        currentLoopMediaPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        Log.d("GAME", "Game activity stopped")
        removeAllCallbacks(resetState = false)
        startMediaPlayer?.release()
        startMediaPlayer = null
        currentLoopMediaPlayer?.release()
        currentLoopMediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        playing = false
        Log.d("GAME", "Game activity destroyed")
        removeAllCallbacks(resetState = true)
        startMediaPlayer?.release()
        startMediaPlayer = null
        currentLoopMediaPlayer?.release()
        currentLoopMediaPlayer = null
    }

}
