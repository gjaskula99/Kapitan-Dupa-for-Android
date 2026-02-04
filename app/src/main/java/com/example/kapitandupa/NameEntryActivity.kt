package com.example.kapitandupa

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NameEntryActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var submitButton: Button
    private var finalScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)
        Log.d("GAME", "Name entry activity created")

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

        nameInput = findViewById(R.id.name_input)
        submitButton = findViewById(R.id.submit_button)

        // Set up input filters for uppercase and max length
        nameInput.filters = arrayOf(
            InputFilter.AllCaps(),
            InputFilter.LengthFilter(5)
        )

        // Handle submit button click
        submitButton.setOnClickListener {
            submitName()
        }

        // Handle Enter key and IME Done action
        nameInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                submitName()
                true
            } else {
                false
            }
        }

        // Auto-focus the input field
        nameInput.requestFocus()
    }

    private fun submitName() {
        var name = nameInput.text.toString().trim().uppercase()

        // Trim all non-uppercase-letter characters (keep only A-Z)
        name = name.replace("*", "")
        name = name.filter { it in 'A'..'Z' }

        // Check if name is longer than 5 characters
        if (name.length > 5) {
            Toast.makeText(this, "Ah tylko 5 liter wchodzi", Toast.LENGTH_SHORT).show()
            return
        }

        // Name cannot be empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Wpisz login twardzielu", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for special name "BOMBA"
        if (name == "BOMBA") {
            Toast.makeText(this, "Kapitan Bomba i Kapitan Dupa to to samo", Toast.LENGTH_LONG).show()
            return
        }


        // Save to leaderboard
        val entry = HighScoreEntry(score = finalScore, name = name)
        LeaderboardManager.saveScore(this, entry)

        // Navigate to leaderboard
        val intent = Intent(this, LeaderboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
