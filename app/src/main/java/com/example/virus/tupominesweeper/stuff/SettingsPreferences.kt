package com.example.virus.tupominesweeper.stuff

import android.content.Context
import com.example.virus.tupominesweeper.viewmodels.SettingsViewModel

object SettingsPreferences {
    internal val NAME = "settings"
    private val DIFF_KEY = "diff"
    private val ROWS_KEY = "rows"
    private val COLS_KEY = "cols"
    private val MINES_KEY = "mines"
    internal val THEME_KEY = "theme"
    internal val VIBRATION_KEY = "vibration"
    private val INVERT_KEY = "invert"

    fun saveVibration(context: Context, vibration: Boolean) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(VIBRATION_KEY, vibration).apply()
    }

    fun saveInvert(context: Context, invert: Boolean) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(INVERT_KEY, invert).apply()
    }

    fun saveGameSettings(context: Context,
        diff: Int, rows: Int, cols: Int, mines: Int
    ) {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        sharedPref.edit()
            .putInt(DIFF_KEY, diff)
            .putInt(ROWS_KEY, rows)
            .putInt(COLS_KEY, cols)
            .putInt(MINES_KEY, mines)
            .apply()
    }

    fun loadGameSettings(context: Context): SettingsViewModel.GameSettings {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val diff = sharedPref.getInt(DIFF_KEY, 0)
        val rows = sharedPref.getInt(ROWS_KEY, 9)
        val cols = sharedPref.getInt(COLS_KEY, 9)
        val mines = sharedPref.getInt(MINES_KEY, 10)
        return SettingsViewModel.GameSettings(diff, rows, cols, mines)
    }

    fun loadAppSettings(context: Context): SettingsViewModel.AppSettings {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val theme = sharedPref.getInt(THEME_KEY, 0)
        val vibration = sharedPref.getBoolean(VIBRATION_KEY, true)
        val invert = sharedPref.getBoolean(INVERT_KEY, false)
        return SettingsViewModel.AppSettings(theme, vibration, invert)
    }
}