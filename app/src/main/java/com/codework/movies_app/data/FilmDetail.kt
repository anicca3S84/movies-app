package com.codework.movies_app.data

import com.squareup.moshi.Json

data class FilmDetail (
    val id: Int,
    val name: String,
    val slug: String,
    @Json(name = "origin_name")
    val originName: String,
    @Json(name = "poster_url")
    val posterUrl: String,
    @Json(name = "thumb_url")
    val thumbUrl: String,
    val year: Int,
    val type: String,
    val content: String,
    val status: String,
    val time: String,
    @Json(name = "episode_current")
    val episodeCurrent: String,
    @Json(name = "episode_total")
    val episodeTotal: String,
    val view: Int,
    val actor: List<String>,
    val director: List<String>,
    val categories: List<Category>,
    val countries: List<Country>,
    val episodes: List<Episode>,
    val comments: List<Comment>,
    val favorites: List<Favorites>,
    val modified: Modified,
)