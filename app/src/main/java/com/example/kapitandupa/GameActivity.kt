package com.example.kapitandupa

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        val button = this.findViewById<Button>(R.id.rypanie)
        // Disable button initially (greyed out)
        button.isEnabled = false
        button.alpha = 0.5f
        button.setOnClickListener{
            if(ready){
                ryp()
            }
        }
        val scoreTXT = this.findViewById<TextView>(R.id.scoreTXT)
        scoreTXT.setText("SCORE")
        val score = this.findViewById<TextView>(R.id.score)
        score.setText("0")

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

        val fiut1 = this.findViewById<ImageView>(R.id.fiut1)
        val fiut2 = this.findViewById<ImageView>(R.id.fiut2)
        val fiut3 = this.findViewById<ImageView>(R.id.fiut3)
        val fiut4 = this.findViewById<ImageView>(R.id.fiut4)
        val fiut5 = this.findViewById<ImageView>(R.id.fiut5)
        val fiut6 = this.findViewById<ImageView>(R.id.fiut6)
        val fiut7 = this.findViewById<ImageView>(R.id.fiut7)
        val fiut8 = this.findViewById<ImageView>(R.id.fiut8)
        val fiut9 = this.findViewById<ImageView>(R.id.fiut9)
        val fiut10 = this.findViewById<ImageView>(R.id.fiut10)
        fiut1.setImageResource(R.drawable.lotos_1)
        fiut2.setImageResource(R.drawable.lotos_1)
        fiut3.setImageResource(R.drawable.lotos_1)
        fiut4.setImageResource(R.drawable.lotos_1)
        fiut5.setImageResource(R.drawable.lotos_1)
        fiut6.setImageResource(R.drawable.lotos_1)
        fiut7.setImageResource(R.drawable.lotos_1)
        fiut8.setImageResource(R.drawable.lotos_1)
        fiut9.setImageResource(R.drawable.lotos_1)
        fiut10.setImageResource(R.drawable.lotos_1)

        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed(Runnable {
            ready = true
            // Enable button when ready
            val button = findViewById<Button>(R.id.rypanie)
            button.isEnabled = true
            button.alpha = 1.0f
            Log.d("GAME", "Ready set to $ready")
            timer()
        }, 10000)
    }

    fun ryp() {
        Log.d("GAME", "Rypanie karabinem")
        val dupa = this.findViewById<ImageView>(R.id.dupa)
        val piotrek = this.findViewById<ImageView>(R.id.piotrek)
        val score = this.findViewById<TextView>(R.id.score)
        score.setText(points.toString())
        points += 1 * stage
        Log.d("GAME", "Points set to $points")
        //var mediaPlayer = MediaPlayer.create(this, R.raw.rypanie)
        //mediaPlayer.start()
        dupa.setImageResource(R.drawable.dupa_2)
        piotrek.setImageResource(R.drawable.pioterk_2)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            dupa.setImageResource(R.drawable.dupa_1)
            piotrek.setImageResource(R.drawable.piotrek_1)
        }, 50)
    }

    var stage : Int = 0
    var playing : Boolean = true
    var toGameOver: Boolean = false
    private var isPaused: Boolean = false
    private var wasAudioPlaying: Boolean = false

    fun gameover() {
        playing = false
        val intent = Intent(this, GameOver::class.java).apply {  }
        startActivity(intent)
    }

    fun timer() {
        // Increment stage
        stage += 1
        Log.d("GAME", "Stage is $stage, Points: $points")

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
        val fiutId = resources.getIdentifier("fiut$stage", "id", packageName)
        val fiut = findViewById<ImageView>(fiutId)
        fiut?.setImageResource(R.drawable.lotos_2)

        // Check if player met the previous stage's threshold
        if (stage > 1 && points < previousThreshold) {
            Log.d("GAME", "Failed stage ${stage - 1}: $points < $previousThreshold")
            toGameOver = true
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

        // Schedule next stage
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed(Runnable {
            if (isFinishing || isPaused) {
                return@Runnable
            }
            Log.d("GAME", "Playing is $playing")
            if (playing == true && !isPaused) {
                timer() // Recursive call for next stage
            }
        }, delay)

        // Start audio or trigger game over (only if not paused)
        if(toGameOver) {
            gameover()
        } else if (!isPaused) {
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
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "Game activity paused")
        isPaused = true

        // Track if audio was playing and pause it
        wasAudioPlaying = (startMediaPlayer?.isPlaying == true) ||
                          (currentLoopMediaPlayer?.isPlaying == true)

        startMediaPlayer?.pause()
        currentLoopMediaPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        Log.d("GAME", "Game activity stopped")
        startMediaPlayer?.release()
        startMediaPlayer = null
        currentLoopMediaPlayer?.release()
        currentLoopMediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        playing = false
        Log.d("GAME", "Game activity destroyed")
        startMediaPlayer?.release()
        startMediaPlayer = null
        currentLoopMediaPlayer?.release()
        currentLoopMediaPlayer = null
    }

}