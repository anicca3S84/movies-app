package com.codework.movies_app.data

data class ApiResponse(
    val status: Boolean = false,
    val items: List<Item> = emptyList(),
    val pagination: Pagination = Pagination()
)