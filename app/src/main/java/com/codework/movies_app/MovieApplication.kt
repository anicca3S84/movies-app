package com.codework.movies_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.codework.movies_app.network.MarsApi
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MovieApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Check for INTERNET permission at runtime
        if(!FirebaseApp.getApps(this).isEmpty()) {
            Log.d("Firebase", "Firebase initialized successfully")
        } else {
            Log.e("Firebase", "Firebase initialization failed")
        }
        createNotificationChannel()
        callApiOnAppStart()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id";
            val channelName = "Thông báo chính"
            val channelDescription = "Kênh thông báo cho ứng dụng Movies"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
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