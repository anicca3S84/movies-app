package com.codework.movies_app.utils

sealed class ValidationInfo {
    object Success: ValidationInfo()
    class Error(val message: String): ValidationInfo()
}

data class RegisterFieldState(
    val email: ValidationInfo,
    val username: ValidationInfo,
    val password: ValidationInfo,
    val confirmPassword: ValidationInfo
)

data class LoginFieldState(
    val email: ValidationInfo,
    val password: ValidationInfo
)