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
        dupa.setImageResource(R.drawable.dupa_2)
        piotrek.setImageResource(R.drawable.pioterk_2)
        val handler = Handler()
        handler.postDelayed(Runnable {
            dupa.setImageResource(R.drawable.dupa_1)
            piotrek.setImageResource(R.drawable.piotrek_1)
        }, 100)
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAME", "Activity resumed")
        var mediaPlayer = MediaPlayer.create(this, R.raw.start)
        mediaPlayer.start()
        Log.d("AUDIO", "Media player started - raw.start")
        Log.d("AUDIO", mediaPlayer.isPlaying.toString())

        val min = 20
        val max = 80
        val random: Int = Random().nextInt(max - min + 1) + min
    }

}