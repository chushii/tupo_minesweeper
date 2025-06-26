package com.example.virus.tupominesweeper.stuff

import android.content.Context
import com.example.virus.tupominesweeper.viewmodels.MainpageViewModel
import com.google.gson.Gson

object GameStatePreferences {
    private val NAME = "game_state"
    private val JSON_KEY = NAME
    private val TIME_KEY = "time_spent"

    fun saveGameState(context: Context, state: MainpageViewModel.GameState?) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(state)
        sharedPref.edit().putString(JSON_KEY, json).apply()
    }

    fun loadGameState(context: Context): MainpageViewModel.GameState? {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString(JSON_KEY, null)
        return json?.let { gson.fromJson(json, MainpageViewModel.GameState::class.java) }
    }

    fun deleteSavedGame(context: Context) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit().remove(JSON_KEY).apply()
    }

    fun saveTimeSpent(context: Context, time: Long) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putLong(TIME_KEY, time).apply()
    }

    fun loadTimeSpent(context: Context): Long {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sharedPref.getLong(TIME_KEY, 0)
    }
}