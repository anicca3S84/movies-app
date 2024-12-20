package com.codework.movies_app.request

data class SaveTokenRequest(
    val username: String,
    val fcmToken: String
)