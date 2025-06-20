package com.example.virus.tupominesweeper.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainpageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Главный экран"
    }
    val text: LiveData<String> = _text
}