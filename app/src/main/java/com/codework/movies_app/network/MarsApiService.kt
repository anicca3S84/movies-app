package com.codework.movies_app.network

import com.codework.movies_app.dto.UserDto
import com.google.android.gms.common.api.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MarsApiService {
    //user
    @POST("/users/register")
    suspend fun insertUser(@Body userDto: UserDto): UserDto
}