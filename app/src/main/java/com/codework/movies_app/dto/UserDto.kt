package com.codework.movies_app.dto

data class UserDto(
    val uid: String,
    val email: String,
    val userName: String,
    val fcmToken: String
)