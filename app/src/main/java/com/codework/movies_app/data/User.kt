package com.codework.movies_app.data

data class User(
    val email: String,
    val password: String,
    var imagePath: String = "",
    var username: String = ""
){
    constructor(): this("", "", "", "")
}