package com.codework.movies_app.viewmodes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedCountViewModel(application: Application) : AndroidViewModel(application) {
    private val _count = MutableLiveData(100)
    val count: LiveData<Int> = _count

    fun increaseCount(): Int {
        _count.value = (_count.value ?: 0) + 1
        return _count.value ?: 0 // Trả về giá trị mới của count
    }
}