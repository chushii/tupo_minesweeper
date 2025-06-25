package com.example.virus.tupominesweeper.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainpageViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        const val KEY_ROWS = "rows"
        const val KEY_COLS = "cols"
        const val KEY_MINES = "mines"
    }

    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> get() = _gameState

    init {
        val rows = savedStateHandle.get<Int>(KEY_ROWS) ?: -1
        val cols = savedStateHandle.get<Int>(KEY_COLS) ?: -1
        val mines = savedStateHandle.get<Int>(KEY_MINES) ?: -1
        if (rows > 0 && cols > 0 && mines > 0) {
            restoreGame(rows, cols, mines)
        }
    }

    fun startNewGame(rows: Int, cols: Int, mineCount: Int) {
        savedStateHandle.set(KEY_ROWS, rows)
        savedStateHandle.set(KEY_COLS, cols)
        savedStateHandle.set(KEY_MINES, mineCount)
        resetGame(rows, cols, mineCount)
    }

    private fun restoreGame(rows: Int, cols: Int, mineCount: Int) {
        resetGame(rows, cols, mineCount)
    }

    private fun resetGame(rows: Int, cols: Int, mineCount: Int) {
        val cells = Array(rows) { Array(cols) { Cell() } }
        _gameState.postValue(GameState(rows, cols, mineCount, cells))
    }

    data class GameState(
        val rows: Int,
        val cols: Int,
        val mineCount: Int,
        val cells: Array<Array<Cell>>,
        val gameEnded: Boolean = false,
        val hasWon: Boolean = false
    )

    data class Cell(
        var mine: Boolean = false,
        var adjacentMines: Int = 0,
        var revealed: Boolean = false,
        var flagged: Boolean = false
    )
}