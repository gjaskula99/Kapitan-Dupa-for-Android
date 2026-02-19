package com.example.kapitandupa

data class HighScoreEntry(
    val score: Int,
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)
