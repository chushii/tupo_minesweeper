package com.example.virus.tupominesweeper

import android.content.Context
import androidx.core.content.ContextCompat
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.virus.tupominesweeper.stuff.GameStatePreferences
import com.example.virus.tupominesweeper.stuff.SettingsPreferences
import com.example.virus.tupominesweeper.stuff.VibratorControl
import com.example.virus.tupominesweeper.viewmodels.MainpageViewModel
import com.example.virus.tupominesweeper.viewmodels.SettingsViewModel
import java.util.Random
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class FieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{
    private var baseCellSize = 60.dpToPx(resources.displayMetrics.density)
    private var cellSize: Float = baseCellSize
    private var diff = 0
    private var rows = 9
    private var cols = 9
    private var mineCount = 10

    private lateinit var cells: Array<Array<MainpageViewModel.Cell>>

    private var offsetX = 0f
    private var offsetY = 0f

    private var gameEnded = false
    private var hasWon = false
    private var minesGenerated = false
    private var firstClickRow = -1
    private var firstClickCol = -1

    private var invertControls = false

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    private val paint = Paint()

    private var viewModel: MainpageViewModel? = null

    var startTime = -1L
    var isTimerRunning = false

    val col_cellClosed = ContextCompat.getColor(context, R.color.field_cellClosed)
    val col_cellOpened = ContextCompat.getColor(context, R.color.field_cellOpened)
    val col_grid = ContextCompat.getColor(context, R.color.field_grid)
    val col_num1 = ContextCompat.getColor(context, R.color.field_num1)
    val col_num2 = ContextCompat.getColor(context, R.color.field_num2)
    val col_num3 = ContextCompat.getColor(context, R.color.field_num3)
    val col_num4 = ContextCompat.getColor(context, R.color.field_num4)
    val col_num5 = ContextCompat.getColor(context, R.color.field_num5)
    val col_num6 = ContextCompat.getColor(context, R.color.field_num6)
    val col_num7 = ContextCompat.getColor(context, R.color.field_num7)
    val col_num8 = ContextCompat.getColor(context, R.color.field_num8)

    val ic_flag = ContextCompat.getDrawable(context, R.drawable.ic_flag)
    val ic_mine = ContextCompat.getDrawable(context, R.drawable.ic_mine)

    init {
        ic_flag?.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
        ic_mine?.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
        ic_flag?.setTint(ContextCompat.getColor(context, R.color.field_num7))
        ic_mine?.setTint(ContextCompat.getColor(context, R.color.field_num7))
    }

    fun setViewModel(viewModel: MainpageViewModel) {
        this.viewModel = viewModel
    }

    fun restoreGame(state: MainpageViewModel.GameState) {
        rows = state.rows
        cols = state.cols
        mineCount = state.mineCount
        cells = state.cells
        gameEnded = state.gameEnded
        hasWon = state.hasWon
        minesGenerated = state.minesGenerated
        invalidate()
    }

    fun resetGame(settings: SettingsViewModel.GameSettings) {
        diff = settings.diff
        rows = settings.rows
        cols = settings.cols
        mineCount = settings.mineCount
        cellSize = baseCellSize
        gameEnded = false
        hasWon = false
        minesGenerated = false
        firstClickRow = -1
        firstClickCol = -1
        cells = Array(rows) { Array(cols) { MainpageViewModel.Cell() } }
        viewModel!!.resetGame(rows, cols, mineCount)
        invalidate()
    }

    fun setInvertControls(invert: Boolean) {
        this.invertControls = invert
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(offsetX, offsetY)

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                drawCell(canvas, row, col)
            }
        }

        if (gameEnded) {
            drawGameOverOverlay(canvas)
        }

        canvas.restore()
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val cell = cells[row][col]

        when {
            cell.revealed -> {
                paint.color = col_cellOpened
            }
            cell.flagged -> {
                paint.color = col_cellClosed
            }
            else -> {
                paint.color = col_cellClosed
            }
        }

        canvas.drawRect(
            col * cellSize,
            row * cellSize,
            (col + 1) * cellSize,
            (row + 1) * cellSize,
            paint
        )

        if (cell.flagged) {
            if (cell.mine && gameEnded)
                ic_flag?.setTint(ContextCompat.getColor(context, R.color.field_num2))
            else if (!cell.mine && gameEnded)
                ic_flag?.setTint(ContextCompat.getColor(context, R.color.field_num3))
            ic_flag?.let { flag ->
                flag.setBounds(
                    (col * cellSize).toInt(),
                    (row * cellSize).toInt(),
                    ((col + 1) * cellSize).toInt(),
                    ((row + 1) * cellSize).toInt()
                )
                flag.draw(canvas)
                ic_flag?.setTint(ContextCompat.getColor(context, R.color.field_num7))
            }
        }

        paint.color = col_grid
        paint.strokeWidth = 5f * (cellSize / baseCellSize)

        // Горизонтальные линии
        canvas.drawLine(
            col * cellSize,
            row * cellSize,
            (col + 1) * cellSize,
            row * cellSize,
            paint
        )
        canvas.drawLine(
            col * cellSize,
            (row + 1) * cellSize,
            (col + 1) * cellSize,
            (row + 1) * cellSize,
            paint
        )

        // Вертикальные линии
        canvas.drawLine(
            col * cellSize,
            row * cellSize,
            col * cellSize,
            (row + 1) * cellSize,
            paint
        )
        canvas.drawLine(
            (col + 1) * cellSize,
            row * cellSize,
            (col + 1) * cellSize,
            (row + 1) * cellSize,
            paint
        )

        if (cell.revealed) {
            if (cell.mine && !cell.flagged) {
                ic_mine?.let { mine ->
                    mine.setBounds(
                        (col * cellSize).toInt(),
                        (row * cellSize).toInt(),
                        ((col + 1) * cellSize).toInt(),
                        ((row + 1) * cellSize).toInt()
                    )
                    mine.draw(canvas)
                }
            } else if (cell.adjacentMines > 0) {
                paint.textSize = 100f * (cellSize / baseCellSize)
                paint.color = when (cell.adjacentMines) {
                    1 -> col_num1
                    2 -> col_num2
                    3 -> col_num3
                    4 -> col_num4
                    5 -> col_num5
                    6 -> col_num6
                    7 -> col_num7
                    else -> col_num8
                }
                canvas.drawText(
                    "${cell.adjacentMines}",
                    col * cellSize + (cellSize / 2.8f),
                    row * cellSize + (cellSize / 1.4f),
                    paint
                )
            }
        }
    }

    private fun drawGameOverOverlay(canvas: Canvas) {
        paint.color = android.graphics.Color.parseColor("#88000000")
        canvas.drawRect(0f, 0f, cols * cellSize, rows * cellSize, paint)

        paint.textSize = 100f * (cellSize / baseCellSize)
        paint.color = android.graphics.Color.WHITE
        val text = if (hasWon) "Победа!" else "ПОМЕР"

        val textWidth = paint.measureText(text)
        canvas.drawText(
            text,
            (cols * cellSize - textWidth) / 2,
            (rows * cellSize) / 2,
            paint
        )

        GameStatePreferences.deleteSavedGame(context)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val focusX = detector.focusX
            val focusY = detector.focusY

            val oldCellSize = cellSize
            cellSize *= detector.scaleFactor
            cellSize = cellSize.coerceIn(baseCellSize * 0.5f..baseCellSize * 3f)

            offsetX = focusX - ((focusX - offsetX) / oldCellSize) * cellSize
            offsetY = focusY - ((focusY - offsetY) / oldCellSize) * cellSize

            ic_flag?.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())
            ic_mine?.setBounds(0, 0, cellSize.toInt(), cellSize.toInt())

            invalidate()
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            offsetX -= distanceX
            offsetY -= distanceY

            val padding = cellSize * 3f
            val fieldWidth = cols * cellSize
            val fieldHeight = rows * cellSize

            offsetX = max(offsetX, -(fieldWidth - width + padding))
            offsetX = min(offsetX, padding)

            offsetY = max(offsetY, -(fieldHeight - height + padding))
            offsetY = min(offsetY, padding)

            invalidate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            handleCellTap(e.x, e.y)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            handleCellLongPress(e.x, e.y)
        }
    }

    private fun roundAwayFromZero(value: Float): Int {
        return if (value > 0) {
            value.toInt()
        } else if (value < 0) {
            floor(value.toDouble()).toInt()
        } else {
            0
        }
    }

    private fun handleCellTap(x: Float, y: Float) {
        if (gameEnded) return

        val realX = x - offsetX
        val realY = y - offsetY

        val row = roundAwayFromZero(realY / cellSize)
        val col = roundAwayFromZero(realX / cellSize)

        if (row in 0 until rows && col in 0 until cols) {
            if (!minesGenerated) {
                generateMinesAvoiding(row, col)
                calculateNumbers()
                minesGenerated = true
                viewModel!!.updateMinesGenerated()
                firstClickRow = row
                firstClickCol = col
            }

            if (invertControls) {
                flagCell(row, col)
            } else {
                revealCell(row, col)
            }
        }
    }

    private fun handleCellLongPress(x: Float, y: Float) {
        if (gameEnded) return

        val realX = x - offsetX
        val realY = y - offsetY

        val row = roundAwayFromZero(realY / cellSize)
        val col = roundAwayFromZero(realX / cellSize)

        if (row in 0 until rows && col in 0 until cols) {
            if (!minesGenerated) {
                generateMinesAvoiding(row, col)
                calculateNumbers()
                minesGenerated = true
                viewModel!!.updateMinesGenerated()
                firstClickRow = row
                firstClickCol = col
            }

            if (invertControls) {
                revealCell(row, col)
            } else {
                flagCell(row, col)
            }
        }
    }

    private fun generateMinesAvoiding(rowAvoid: Int, colAvoid: Int) {
        val random = Random()
        var placed = 0
        while (placed < mineCount) {
            val row = random.nextInt(rows)
            val col = random.nextInt(cols)
            val isNearFirstClick = abs(row - rowAvoid) <= 1 && abs(col - colAvoid) <= 1
            if (isNearFirstClick || cells[row][col].mine) continue

            cells[row][col].mine = true
            placed++
        }
    }

    private fun revealCell(row: Int, col: Int) {
        val cell = cells[row][col]
        if (cell.revealed || cell.flagged) return

        cell.revealed = true
        cell.flagged = false

        if (cell.mine) {
            VibratorControl.vibrateLong(context)
            gameEnded = true
            hasWon = false
            viewModel!!.updateGameOver(gameEnded, hasWon)
            revealAllMines()
            invalidate()
            return
        }

        if (cell.adjacentMines == 0) {
            for (r in -1..1) {
                for (c in -1..1) {
                    if (r == 0 && c == 0) continue
                    val newRow = row + r
                    val newCol = col + c
                    if (newRow in 0 until rows && newCol in 0 until cols) {
                        revealCell(newRow, newCol)
                    }
                }
            }
        }

        checkWinCondition(row, col)
        invalidate()
    }

    private fun flagCell(row: Int, col: Int) {
        val cell = cells[row][col]
        if (!cell.revealed) {
            VibratorControl.vibrateShort(context)
            cell.flagged = !cell.flagged
            checkWinCondition(row, col)
            invalidate()
        }
    }

    private fun checkWinCondition(row: Int, col: Int) {
        val allMinesFlagged = cells.flatten().all {
            if (it.mine) it.flagged else true
        }

        val allFlagsAreCorrect = cells.flatten().none {
            it.flagged && !it.mine
        }

        val allRevealed = cells.flatten().all {
            it.revealed || it.mine
        }

        if ((allMinesFlagged || allRevealed) && allFlagsAreCorrect) {
            gameEnded = true
            hasWon = true
            viewModel!!.updateGameOver(gameEnded, hasWon)
            invalidate()
        }
        else {
            val cell = cells[row][col]
            val newCells = cells.map { it.copyOf() }.toTypedArray()
            newCells[row][col] = cell.copy()
            viewModel!!.updateGameStateCells(newCells)
            val e = 5
        }
    }

    private fun revealAllMines() {
        cells.forEach { row ->
            row.forEach {
                if (it.mine) it.revealed = true
            }
        }
    }

    private fun calculateNumbers() {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (cells[row][col].mine) continue

                var count = 0
                for (r in -1..1) {
                    for (c in -1..1) {
                        val newRow = row + r
                        val newCol = col + c
                        if (newRow in 0 until rows && newCol in 0 until cols) {
                            if (cells[newRow][newCol].mine) {
                                count++
                            }
                        }
                    }
                }
                cells[row][col].adjacentMines = count
            }
        }
    }

    private fun Int.dpToPx(density: Float): Float {
        return this * density + 0.5f
    }
}