package com.codework.movies_app.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val email: String,
    var username: String = "",
    val password: String,
    var imagePath: String = "",
    var phone: String = "",
    var gender: String = ""

): Parcelable {
    constructor(): this("", "", "", "")
}