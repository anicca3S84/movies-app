package com.codework.movies_app.viewmodes

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.R
import com.codework.movies_app.utils.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object {
        const val MAIN_ACTIVITY = 23
        val ACCOUNT_OPTION_FRAGMENT = R.id.action_introFragment_to_loginFragment
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _navigate.emit(MAIN_ACTIVITY)
            }
        } else if (isButtonClicked) {
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        } else {
            Unit
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }
}