package com.example.virus.tupominesweeper.stuff

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.virus.tupominesweeper.stuff.SettingsPreferences.NAME
import com.example.virus.tupominesweeper.stuff.SettingsPreferences.VIBRATION_KEY

object VibratorControl {
    fun checkVibration(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(VIBRATION_KEY, true)
    }

    fun vibrateShort(context: Context) {
        if (!checkVibration(context)) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(50)
        }
    }

    fun vibrateLong(context: Context) {
        if (!checkVibration(context)) return
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(250)
        }
    }
}