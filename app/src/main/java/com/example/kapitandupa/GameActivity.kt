package com.example.kapitandupa

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
            //if(ready){
                ryp()
            //}
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

    fun playSound() {
        val min = 1
        val max = 5
        val random: Int = Random().nextInt(max - min + 1) + min
        var delay : Long = 13000
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
        }
        val mHandler = Handler()
        mHandler.postDelayed(Runnable {
            if (isFinishing) {
                // Check if the Activity is finishing.
                return@Runnable
            }
            mediaPlayer!!.start()
            if (mKeepPlaying) {
                // play the sound again in 10 seconds
                playSound()
            }
        }, delay)
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")
        var mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.start()
        Log.d("AUDIO", "Media player started - raw.start")
        Log.d("AUDIO", mediaPlayer.isPlaying.toString())

        playSound()
    }

}