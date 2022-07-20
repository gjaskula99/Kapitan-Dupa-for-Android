package com.example.kapitandupa

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GameActivity : AppCompatActivity() {
    var ready : Boolean = false
    var points : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d("GAME", "Activity started")

        val button = this.findViewById<Button>(R.id.rypanie)
        button.setOnClickListener{
            if(ready){
                ryp()
            }
        }
    }

    fun ryp() {
        Log.d("GAME", "Rypanie karabinem")
        val dupa = this.findViewById<ImageView>(R.id.dupa)
        val piotrek = this.findViewById<ImageView>(R.id.piotrek)
        points += 1
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

    var stage : Int = 0
    var delay : Long = 13000

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
        Log.d("AUDIO", "Next playing rypanie for random $random")
        var mediaPlayer: MediaPlayer? = null
        var mKeepPlaying : Boolean = true
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

        stage += 1
        Log.d("GAME", "Stage is $stage")
        when(stage) {
            1 -> fiut1.setImageResource(R.drawable.lotos_2)
            2 -> fiut2.setImageResource(R.drawable.lotos_2)
            3 -> fiut3.setImageResource(R.drawable.lotos_2)
            4 -> fiut4.setImageResource(R.drawable.lotos_2)
            5 -> fiut5.setImageResource(R.drawable.lotos_2)
            6 -> fiut6.setImageResource(R.drawable.lotos_2)
            7 -> fiut7.setImageResource(R.drawable.lotos_2)
            8 -> fiut8.setImageResource(R.drawable.lotos_2)
            9 -> fiut9.setImageResource(R.drawable.lotos_2)
            10 -> fiut10.setImageResource(R.drawable.lotos_2)
            11 -> {
                mKeepPlaying = false
                mediaPlayer!!.stop()
                val intent = Intent(this, GameOver::class.java).apply {  }
                startActivity(intent)
        }
            else -> Log.d("GAME", "Stage out of range - $stage")
        }

        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            if (isFinishing) {
                // Check if the Activity is finishing.
                return@Runnable
            }
            if(stage < 10) mediaPlayer!!.start()
            if (mKeepPlaying) {
                // play the sound again in 10 seconds
                timer()
            }
        }, delay)
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")
        var mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.start()
        Log.d("AUDIO", "Media player started - raw.start")
        //Log.d("AUDIO", mediaPlayer.isPlaying.toString())
        points = 0
        stage = 0
        ready = false

        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            Log.d("GAME", "Ready set to $ready")
            ready = true
            timer()
        }, 10000)
    }

    override fun onStop() {
        super.onStop()

    }

}