package com.codework.movies_app.network

import com.codework.movies_app.data.Film
import com.codework.movies_app.dto.UserDto
import com.google.android.gms.common.api.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MarsApiService {
    //user
    @POST("/users/register")
    suspend fun insertUser(@Body userDto: UserDto): UserDto

    //film detail
    @GET("/danh-sach/phim/{slug}")
    suspend fun getFilmDetail(@Path("slug") slug: String): Film

}