package com.codework.movies_app.data

data class Notification(
    val id: Int,
    val message: String,
    val actionType: String,
    val relatedId: Int?,
    val createdAt: String,
    val read: Boolean
)
