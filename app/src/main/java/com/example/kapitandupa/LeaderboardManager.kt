package com.example.kapitandupa

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LeaderboardManager {
    private const val PREFS_NAME = "kapitan_dupa_leaderboard"
    private const val ENTRIES_KEY = "entries"
    private const val MAX_ENTRIES = 5
    private val gson = Gson()

    /**
     * Save a new score entry to the leaderboard
     */
    fun saveScore(context: Context, entry: HighScoreEntry) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val entries = getTopScores(context).toMutableList()

        entries.add(entry)

        // Sort by score descending, then by timestamp ascending (earlier time wins ties)
        entries.sortWith(compareByDescending<HighScoreEntry> { it.score }
            .thenBy { it.timestamp })

        // Keep only top 5
        val topEntries = entries.take(MAX_ENTRIES)

        // Persist to SharedPreferences
        val json = gson.toJson(topEntries)
        prefs.edit().putString(ENTRIES_KEY, json).apply()
    }

    /**
     * Get the top scores from the leaderboard
     */
    fun getTopScores(context: Context): List<HighScoreEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ENTRIES_KEY, null) ?: return emptyList()

        return try {
            val type = object : TypeToken<List<HighScoreEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Check if a score qualifies for the leaderboard (top 5)
     */
    fun isHighScore(context: Context, score: Int): Boolean {
        val entries = getTopScores(context)

        // If fewer than 5 entries, any score qualifies
        if (entries.size < MAX_ENTRIES) {
            return true
        }

        // Check if score is higher than the lowest entry
        val lowestScore = entries.minOfOrNull { it.score } ?: 0
        return score > lowestScore
    }

    /**
     * Clear all leaderboard entries
     */
    fun clearLeaderboard(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(ENTRIES_KEY).apply()
    }
}
