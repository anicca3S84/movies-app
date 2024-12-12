package com.codework.movies_app

import android.app.Application
import android.util.Log
import com.codework.movies_app.network.MarsApi
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MovieApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        callApiOnAppStart()
    }

    private fun callApiOnAppStart() {
        val apiService = MarsApi.retrofitService

        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService.getListNewFilms()
                Log.d("MyApplication", "API called successfully")
            } catch (e: Exception) {
                Log.e("MyApplication", "Error calling API: ${e.message}")
            }
        }
    }
}