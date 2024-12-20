package com.codework.movies_app.data

import com.codework.movies_app.dto.UserCommentResponse

data class Comment(
    val id: Int,
    val content: String,
    val createdAt: String,
    val user: UserCommentResponse
)
