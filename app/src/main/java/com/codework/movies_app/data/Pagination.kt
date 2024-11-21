package com.codework.movies_app.data

data class Pagination(
    val totalItems: Int,
    val totalItemsPerPage: Int,
    val currentPage: Int,
    val totalPages: Int
)
