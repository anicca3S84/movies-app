package com.codework.movies_app.network

import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Comment
import com.codework.movies_app.data.Episode
import com.codework.movies_app.data.Favorites
import com.codework.movies_app.data.Item
import com.codework.movies_app.data.FilmDetail
import com.codework.movies_app.data.Notification
import com.codework.movies_app.dto.UserDto
import com.codework.movies_app.request.CommentRequest
import com.codework.movies_app.request.FavoriteRequest
import com.codework.movies_app.request.SaveTokenRequest
import com.google.protobuf.Api
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MarsApiService {
    //user
    @GET("/danh-sach/phim-moi-cap-nhat")
    suspend fun getNewFilms(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ) : ApiResponse

    @GET("/danh-sach/search")
    suspend fun getSearchFilms(
        @Query("keyword") keyword: String
    ) : List<Item>
    @POST("/users/register")
    suspend fun insertUser(@Body userDto: UserDto): UserDto

    @POST("/notification/saveToken")
    suspend fun saveToken(@Body saveTokenRequest: SaveTokenRequest): String

    //film detail
    @GET("/danh-sach/phim/{slug}")
    suspend fun getFilmDetail(@Path("slug") slug: String, @Query("username") username: String? = null): FilmDetail

    //film
    @GET("/danh-sach/filterByCategory/{category}")
    suspend fun getFilmsByCategory(@Path("category") category: String): ApiResponse

    @GET("/danh-sach/filterByType/{type}")
    suspend fun getFilmsByType(@Path("type") type: String): ApiResponse

    @GET("/films/{slug}/episodes")
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

    @GET("/notification/unread/{userId}")
    suspend fun getNotification(@Path("userId") userId: String): List<Notification>

    @GET("/danh-sach/phim-moi-cap-nhat")
    suspend fun getListNewFilms()

    @DELETE("/notification/delete/{notificationId}")
    suspend fun deleteNotification(@Path("notificationId") notificationId: Int)





}