package com.codework.movies_app.data

import com.squareup.moshi.Json

data class Episode(
    val name: String,
    val slug: String,
    val filename: String,
    @Json(name = "link_embed")
    val linkEmbed: String,
    @Json(name = "link_m3u8")
    val linkM3u8: String
)