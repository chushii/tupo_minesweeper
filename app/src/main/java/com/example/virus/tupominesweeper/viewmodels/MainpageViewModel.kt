package com.example.virus.tupominesweeper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainpageViewModel : ViewModel() {
    private val _game_state = MutableLiveData<GameState?>()
    val game_state: LiveData<GameState?> get() = _game_state

    var gameEnded = false
        private set

    fun loadGameState(state: GameState?) {
        _game_state.value = state
    }

    fun resetGame(diff: Int, rows: Int, cols: Int, mineCount: Int) {
        gameEnded = false
        val cells = Array(rows) { Array(cols) { Cell() } }
        _game_state.value = GameState(diff, rows, cols, mineCount, cells,
            false, false, false, false)
    }

    fun updateGameStateCells(cells: Array<Array<Cell>>) {
        _game_state.value = _game_state.value?.copy(
            cells = cells
        )
    }

    fun updateMinesGenerated() {
        _game_state.value = _game_state.value?.copy(
            minesGenerated = true
        )
    }

    fun updateGameOver(gameEnded: Boolean, hasWon: Boolean) {
        _game_state.value = _game_state.value?.copy(
            gameEnded = gameEnded,
            hasWon = hasWon
        )
        this.gameEnded = true
    }

    fun getMinesCounter(cells: Array<Array<Cell>>, mines: Int): Int {
        val flags = cells.sumOf { row ->
            row.count { it.flagged }
        }
        return mines - flags
    }

    data class GameState(
        val diff: Int,
        val rows: Int,
        val cols: Int,
        val mineCount: Int,
        val cells: Array<Array<Cell>>,
        val gameEnded: Boolean,
        val hasWon: Boolean,
        val recordSet: Boolean,
        val minesGenerated: Boolean
    )

    data class Cell(
        var mine: Boolean = false,
        var adjacentMines: Int = 0,
        var revealed: Boolean = false,
        var flagged: Boolean = false
    )
}