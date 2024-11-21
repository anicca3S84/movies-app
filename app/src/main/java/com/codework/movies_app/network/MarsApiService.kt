package com.codework.movies_app.network

import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Comment
import com.codework.movies_app.data.Episode
import com.codework.movies_app.data.Favorites
import com.codework.movies_app.data.Item
import com.codework.movies_app.data.FilmDetail
import com.codework.movies_app.dto.UserDto
import com.codework.movies_app.request.CommentRequest
import com.codework.movies_app.request.FavoriteRequest
import com.google.protobuf.Api
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MarsApiService {
    //user
    @POST("/users/register")
    suspend fun insertUser(@Body userDto: UserDto): UserDto

    //film detail
    @GET("/danh-sach/phim/{slug}")
    suspend fun getFilmDetail(@Path("slug") slug: String): FilmDetail

    //film
    @GET("/danh-sach/filterByCategory/{category}")
    suspend fun getFilmsByCategory(@Path("category") category: String): ApiResponse

    @GET("/danh-sach/filterByType/{type}")
    suspend fun getFilmsByType(@Path("type") type: String): ApiResponse

    @GET("/episodes/film/list-episodes/{slug}")
    suspend fun getLisEpisodesBySlug(@Path("slug") type: String): List<Episode>

    @POST("/danh-sach/addFavorite")
    suspend fun addFavFilm(@Body favoriteRequest: FavoriteRequest): Favorites

    @DELETE("/danh-sach/removeFavorite/{username}/{slug}")
    suspend fun deleteFavFilm(@Path("username") username: String, @Path("slug") slug: String): String

    @GET("/danh-sach/favorite/{username}")
    suspend fun getListOfFavFilm(@Path("username") username: String): ApiResponse

    @POST("/comments")
    suspend fun postComment(@Body commentRequest: CommentRequest): Comment

    @DELETE("/comments/{id}")
    suspend fun removeComment(
        @Path("id") id: Int,
        @Query("username") username: String
    ): String

    @GET("/history/{username}")
    suspend fun getHistory(@Path("username") username: String): ApiResponse




}