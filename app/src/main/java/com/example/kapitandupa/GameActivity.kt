package com.example.kapitandupa

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GameActivity : AppCompatActivity() {
    var ready : Boolean = false
    var points : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d("GAME", "Activity created")

        val button = this.findViewById<Button>(R.id.rypanie)
        button.setOnClickListener{
            if(ready){
                ryp()
            }
        }
        val scoreTXT = this.findViewById<TextView>(R.id.scoreTXT)
        scoreTXT.setText("SCORE")
        val score = this.findViewById<TextView>(R.id.score)
        score.setText("0")

        var mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.start()
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

        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            ready = true
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
        val handler = Handler()
        handler.postDelayed(Runnable {
            dupa.setImageResource(R.drawable.dupa_1)
            piotrek.setImageResource(R.drawable.piotrek_1)
        }, 100)
    }

    var stage : Int = 1
    var delay : Long = 13000
    var playing : Boolean = true
    var toGameOver: Boolean = false

    fun gameover() {
        playing = false
        val intent = Intent(this, GameOver::class.java).apply {  }
        startActivity(intent)
    }

    fun timer() {
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

        val min = 1
        val max = 5
        val random: Int = Random().nextInt(max - min + 1) + min
        Log.d("AUDIO", "Playing rypanie for random $random")
        var mediaPlayer: MediaPlayer? = null
        //if(!toGameOver) {
            when(random) {
                1 -> {mediaPlayer = MediaPlayer.create(this, R.raw.rypanie_jakbabe)
                //delay = 10000
            }
                2 -> {mediaPlayer = MediaPlayer.create(this, R.raw.rypanie_kolba)
                //delay = 16000
            }
                3 -> {mediaPlayer = MediaPlayer.create(this, R.raw.rypanie_nie)
                //delay = 7000
            }
                4 -> {mediaPlayer = MediaPlayer.create(this, R.raw.rypanie_nieczuje)
                //delay = 8000
            }
                5 -> {mediaPlayer = MediaPlayer.create(this, R.raw.rypanie_trututututu)
                //delay = 16000
            }
                else -> Log.d("AUDIO", "Random out of range - $random")
            }
        //}

        stage += 1
        Log.d("GAME", "Stage is $stage")
        when(stage) {
            1 -> fiut1.setImageResource(R.drawable.lotos_2)
            2 -> {
                fiut2.setImageResource(R.drawable.lotos_2)
                if(points < 20) toGameOver = true
            }
            3 -> {
                fiut3.setImageResource(R.drawable.lotos_2)
                if(points < 80) toGameOver = true
            }
            4 -> {
                fiut4.setImageResource(R.drawable.lotos_2)
                if(points < 200) toGameOver = true
            }
            5 -> {
                fiut5.setImageResource(R.drawable.lotos_2)
                if(points < 500) toGameOver = true
            }
            6 -> {
                fiut6.setImageResource(R.drawable.lotos_2)
                if(points < 1000) toGameOver = true
            }
            7 -> {
                fiut7.setImageResource(R.drawable.lotos_2)
                if(points < 2000) toGameOver = true
            }
            8 -> {
                fiut8.setImageResource(R.drawable.lotos_2)
                if(points < 4000) toGameOver = true
            }
            9 -> {
                fiut9.setImageResource(R.drawable.lotos_2)
                if(points < 8000) toGameOver = true
            }
            10 -> {
                fiut10.setImageResource(R.drawable.lotos_2)
                if(points < 10000) toGameOver = true
            }
            11 -> {
                mediaPlayer!!.stop()
                gameover()

        }
            else -> Log.d("GAME", "Stage out of range - $stage")
        }

        Log.d("GAME", "Game over is $toGameOver")
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            if (isFinishing) {
                // Check if the Activity is finishing.
                return@Runnable
            }
            Log.d("GAME", "Playing is $playing")
            //if(playing == true) mediaPlayer!!.start()
            if (playing == true) {
                // play the sound again in 10 seconds
                timer()
            }
        }, delay)
        if(toGameOver) gameover()
        else mediaPlayer!!.start()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")
        /*Log.d("AUDIO", "Media player started - raw.start")
        *///Log.d("AUDIO", mediaPlayer.isPlaying.toString())
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAME", "Game activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d("GAME", "Game activity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        playing = false
        Log.d("GAME", "Game activity destroyed")
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}