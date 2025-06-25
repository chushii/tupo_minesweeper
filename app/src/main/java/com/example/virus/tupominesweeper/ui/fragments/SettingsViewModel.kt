package com.example.virus.tupominesweeper.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private val _settings = MutableLiveData<GameSettings>().apply {
        value = GameSettings(
            rows = 20,
            cols = 20,
            mineCount = 10,
            invertControls = false
        )
    }

    val settings: LiveData<GameSettings> = _settings

    fun setFieldSettings(rows: Int, cols: Int, mineCount: Int) {
        _settings.value = _settings.value?.copy(
            rows = rows,
            cols = cols,
            mineCount = mineCount
        )
    }

    fun setInvert(invert: Boolean) {
        _settings.value = _settings.value?.copy(
            invertControls = invert
        )
    }

    data class GameSettings(
        val rows: Int,
        val cols: Int,
        val mineCount: Int,
        val invertControls: Boolean
    )
}