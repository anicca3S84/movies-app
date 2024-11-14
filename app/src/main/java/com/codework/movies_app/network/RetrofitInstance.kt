package com.codework.movies_app.network

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "http://192.168.10.102:8080/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object MarsApi {
    val retrofitService: MarsApiService by lazy {
        try {
            Log.d("MarsApi", "Initializing Retrofit...")
            retrofit.create(MarsApiService::class.java)
        } catch (e: Exception) {
            Log.e("MarsApi", "Retrofit initialization failed", e)
            throw e // Re-throw to propagate the error
        }
    }
}