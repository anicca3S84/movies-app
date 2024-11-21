package com.codework.movies_app.request

data class CommentRequest(
    val content: String,
    val username: String,
    val slug: String
)