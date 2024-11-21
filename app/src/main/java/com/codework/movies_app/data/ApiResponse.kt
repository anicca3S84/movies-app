package com.codework.movies_app.data

data class ApiResponse(
    val status: Boolean,
    val items: List<Item>,
    val pagination: Pagination
)