package com.codework.movies_app.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormatDate {
    companion object {
        fun formatDate(input: String): String {
            return try {
                // Định dạng ban đầu (chuỗi từ API)
                val inputFormatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val outputFormatter =
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm MM/dd/yyyy")
                val dateTime = java.time.LocalDateTime.parse(input, inputFormatter)
                dateTime.format(outputFormatter)
            } catch (e: Exception) {
                input
            }
        }
    }
}