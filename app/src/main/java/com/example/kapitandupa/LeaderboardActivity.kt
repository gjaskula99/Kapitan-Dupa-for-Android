package com.example.kapitandupa

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        Log.d("GAME", "Leaderboard activity created")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        })

        displayLeaderboard()

        val restartButton: Button = findViewById(R.id.restart_button)
        restartButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Wyczyść ranking")
                .setMessage("Czy na pewno chcesz wyczyścić ranking?")
                .setPositiveButton("Tak") { _, _ ->
                    LeaderboardManager.clearLeaderboard(this)
                    Toast.makeText(this, "Ranking wyczyszczony", Toast.LENGTH_SHORT).show()
                    displayLeaderboard()
                }
                .setNegativeButton("Nie", null)
                .show()
        }
    }

    private fun displayLeaderboard() {
        val scoresContainer: LinearLayout = findViewById(R.id.scores_container)
        scoresContainer.removeAllViews()

        val topScores = LeaderboardManager.getTopScores(this)

        if (topScores.isEmpty()) {
            val noScoresText = TextView(this).apply {
                text = "Brak wyników"
                textSize = 24f
                setTextColor(getColor(android.R.color.holo_green_light))
                gravity = Gravity.CENTER
            }
            scoresContainer.addView(noScoresText)
        } else {
            topScores.forEachIndexed { index, entry ->
                val scoreText = TextView(this).apply {
                    text = "${index + 1}. ${entry.name} - ${entry.score}"
                    textSize = 18f
                    setTextColor(getColor(android.R.color.holo_green_light))
                    gravity = Gravity.CENTER
                    setPadding(0, 8, 0, 8)
                }
                scoresContainer.addView(scoreText)
            }
        }
    }
}
