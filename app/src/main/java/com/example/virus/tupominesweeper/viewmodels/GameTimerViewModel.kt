package com.example.virus.tupominesweeper.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameTimerViewModel : ViewModel() {
    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> get() = _elapsedTime

    private var startTime = 0L
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    fun start(initialTime: Long = 0) {
        if (isRunning) return

        startTime = System.currentTimeMillis() - initialTime
        isRunning = true

        runnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    val elapsed = System.currentTimeMillis() - startTime
                    _elapsedTime.postValue(elapsed)
                    handler.postDelayed(this, 1000)
                }
            }
        }

        handler.post(runnable!!)
    }

    fun pause() {
        isRunning = false
        runnable?.let { handler.removeCallbacks(it) }
    }

    fun reset() {
        pause()
        startTime = 0
        _elapsedTime.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        pause()
    }
}