package com.example.virus.tupominesweeper.stuff

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.virus.tupominesweeper.stuff.SettingsPreferences.NAME

object ThemeSwitcher {
    fun loadTheme(c: Context): Int {
        val sharedPref = c.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(SettingsPreferences.THEME_KEY, 0)
    }

    fun setTheme(c: Context, theme: Int) {
        when (theme) {
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        val sharedPref = c.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putInt(SettingsPreferences.THEME_KEY, theme).apply()
    }
}