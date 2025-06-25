package com.example.virus.tupominesweeper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private val _game_settings = MutableLiveData<GameSettings>().apply {
        value = GameSettings(
            diff = 0,
            rows = 20,
            cols = 20,
            mineCount = 40
        )
    }

    private val _app_settings = MutableLiveData<AppSettings>().apply {
        value = AppSettings(
            theme = 0,
            vibration = true,
            invert = false
        )
    }

    val game_settings: LiveData<GameSettings> = _game_settings
    val app_settings: LiveData<AppSettings> = _app_settings

    fun setGameSettings(diff: Int, rows: Int, cols: Int, mineCount: Int) {
        _game_settings.value = _game_settings.value?.copy(
            diff = diff,
            rows = rows,
            cols = cols,
            mineCount = mineCount
        )
    }

    fun setTheme(theme: Int) {
        _app_settings.value = _app_settings.value?.copy(
            theme = theme
        )
    }

    fun setVibration(vibration: Boolean) {
        _app_settings.value = _app_settings.value?.copy(
            vibration = vibration
        )
    }

    fun setInvert(invert: Boolean) {
        _app_settings.value = _app_settings.value?.copy(
            invert = invert
        )
    }

    data class GameSettings(
        val diff: Int,
        val rows: Int,
        val cols: Int,
        val mineCount: Int
    )

    data class AppSettings(
        val theme: Int,
        val vibration: Boolean,
        val invert: Boolean
    )

}