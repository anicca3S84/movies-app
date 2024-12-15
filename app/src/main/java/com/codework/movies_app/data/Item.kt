package com.codework.movies_app.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val id: Int,
    val name: String,
    val slug: String,
    @Json(name = "origin_name")
    val originName: String,
    @Json(name = "poster_url")
    val posterUrl: String,
    @Json(name = "thumb_url")
    val thumbUrl: String,
    val year: Int
): Parcelable