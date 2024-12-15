package com.codework.movies_app.utils

import android.content.Context
import android.content.SharedPreferences

object FavoriteManager {
    private const val PREF_NAME = "favorites_pref"
    private const val KEY_FAVORITES = "key_favorites"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun setFavorite(context: Context, slug: String, isFavorite: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(slug, isFavorite)
        editor.apply()
    }

    fun isFavorite(context: Context, slug: String): Boolean {
        return getPreferences(context).getBoolean(slug, false)
    }
}
