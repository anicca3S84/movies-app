package com.codework.movies_app.request

data class UpdateUserRequest(
    val uid: String,
    val email: String,
    val username: String
)