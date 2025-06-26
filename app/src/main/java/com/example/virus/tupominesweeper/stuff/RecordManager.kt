package com.example.virus.tupominesweeper.stuff

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

object RecordManager {
    private const val NAME = "leaderboard"
    private const val KEY_EASY = "records_easy"
    private const val KEY_MEDIUM = "records_medium"
    private const val KEY_HARD = "records_hard"

    private val gson = Gson()

    data class GameRecord(
        val score: Long,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun saveRecords(context: Context, diff: Int, records: List<GameRecord>) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(records)
        val diffKey = when (diff) {
            0 -> KEY_EASY
            1 -> KEY_MEDIUM
            2 -> KEY_HARD
            else -> ""
        }
        if (diffKey.isNotEmpty()) sharedPref.edit().putString(diffKey, json).apply()
    }

    fun loadRecords(context: Context, diff: Int): List<GameRecord> {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val diffKey = when (diff) {
            0 -> KEY_EASY
            1 -> KEY_MEDIUM
            2 -> KEY_HARD
            else -> ""
        }
        if (diffKey.isEmpty()) return emptyList()
        val json = sharedPref.getString(diffKey, null)
        val type = object : TypeToken<List<GameRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun updateRecords(context: Context, diff: Int, newTime: Long) {
        if (newTime <= 0) return
        val diffKey = when (diff) {
            0 -> KEY_EASY
            1 -> KEY_MEDIUM
            2 -> KEY_HARD
            else -> ""
        }
        if (diffKey.isEmpty()) return
        val existingRecords = loadRecords(context, diff).toMutableList()
        if (existingRecords.any { it.score == newTime }) { return }
        existingRecords.add(GameRecord(score = newTime))
        existingRecords.sortBy { it.score }
        val updatedRecords = existingRecords.take(5).toList()
        saveRecords(context, diff, updatedRecords)
    }

    fun willMyRecordSave(context: Context, diff: Int, newTime: Long): Boolean {
        if (newTime <= 0) return false
        val diffKey = when (diff) {
            0 -> KEY_EASY
            1 -> KEY_MEDIUM
            2 -> KEY_HARD
            else -> ""
        }
        if (diffKey.isEmpty()) return false
        val currentRecords = loadRecords(context, diff).toMutableList()
        if (currentRecords.any { it.score == newTime }) { return false }
        val imaginaryRecords = (currentRecords + GameRecord(score = newTime)).sortedBy { it.score }
        return imaginaryRecords.any { it.score == newTime }
    }
}