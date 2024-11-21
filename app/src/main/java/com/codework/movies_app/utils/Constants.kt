package com.codework.movies_app.utils

import android.content.Context
import android.content.SharedPreferences

object Constants {
    const val USER_COLLECTION = "users"
    const val INTRODUCTION_SP = "IntroductionSP"
    const val INTRODUCTION_KEY = "IntroductionKey"
    private const val PREF_NAME = "UserPrefs"
    private const val KEY_USERNAME = "username"

    // Lấy username từ SharedPreferences
    fun getUsername(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    // Lưu username vào SharedPreferences
    fun saveUsername(context: Context, username: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }


}