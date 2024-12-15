package com.codework.movies_app

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.network.NetworkUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MovieApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (NetworkUtils.isInternetAvailable(this)) {
            callApiOnAppStart()
        } else {
            Toast.makeText(this, "Vui lòng kiểm tra kết nối Internet", Toast.LENGTH_LONG).show()
        }
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