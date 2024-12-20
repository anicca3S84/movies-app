package com.codework.movies_app.utils

import android.util.Patterns

fun validateEmail(email: String): ValidationInfo{
    if(email.isEmpty())
        return ValidationInfo.Error("Email không được để trống")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return ValidationInfo.Error("Email không hợp lệ")
    return ValidationInfo.Success
}

fun validateUserName(userName: String): ValidationInfo{
    if(userName.isEmpty())
        return ValidationInfo.Error("Tên người dùng không được để trống")
    return ValidationInfo.Success
}

fun validatePassword(password: String): ValidationInfo{
    if(password.isEmpty())
        return ValidationInfo.Error("Mật khẩu không được để trống")
    if(password.length < 6)
        return ValidationInfo.Error("Mật khẩu phải có ít nhất 6 ký tự")
    return ValidationInfo.Success
}


fun validateConfirmPassword(password: String, confirmPassword: String): ValidationInfo{
    if(confirmPassword.isEmpty())
        return ValidationInfo.Error("Vui lòng xác nhận mật khẩu của bạn")
    if(confirmPassword != password)
        return ValidationInfo.Error("Mật khẩu xác nhận không khớp")
    return ValidationInfo.Success
}