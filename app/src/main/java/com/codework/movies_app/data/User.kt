package com.codework.movies_app.data

data class User(
    val email: String,
    var username: String = "",
    val password: String,
    var imagePath: String = ""

){
    constructor(): this("", "", "", "")
}