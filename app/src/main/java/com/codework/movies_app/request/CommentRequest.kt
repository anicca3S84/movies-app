package com.codework.movies_app.request

data class CommentRequest(
    val content: String,
    val userId: String,
    val slug: String
)