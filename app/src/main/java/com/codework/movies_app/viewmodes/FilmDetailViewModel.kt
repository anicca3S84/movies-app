package com.codework.movies_app.viewmodes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Category
import com.codework.movies_app.data.Comment
import com.codework.movies_app.data.Favorites
import com.codework.movies_app.data.Item
import com.codework.movies_app.data.FilmDetail
import com.codework.movies_app.data.User
import com.codework.movies_app.firebase.FirebaseCommon
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.request.CommentRequest
import com.codework.movies_app.request.FavoriteRequest
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Constants.USER_COLLECTION
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _filmDetail = MutableStateFlow<Resource<FilmDetail>>(Resource.Unspecified())
    val filmDetail = _filmDetail.asStateFlow()

    private val _videoUrl = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val videoUrl = _videoUrl.asStateFlow()

    private val _sameCategory = MutableStateFlow<Resource<ApiResponse>>(Resource.Unspecified())
    val sameCategory = _sameCategory.asStateFlow()

    private val _listFav = MutableStateFlow<Resource<List<Item>>>(Resource.Unspecified())
    val listFav = _listFav.asStateFlow()

    private val _fav = MutableStateFlow<Resource<Item>>(Resource.Unspecified())
    val fav = _fav.asStateFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _comment = MutableStateFlow<Resource<Comment>>(Resource.Unspecified())
    val comment = _comment.asStateFlow()

    private val _listComment = MutableStateFlow<Resource<List<Comment>>>(Resource.Unspecified())
    val listComment = _listComment.asStateFlow()

    fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun addComment(slug: String, content: String){
        viewModelScope.launch {
            _comment.emit(Resource.Loading())
        }
        viewModelScope.launch {
            try {
                val username = Constants.getUsername(context)
                if(username != null){
                    try {
                        val uid = auth.currentUser?.uid
                        uid?.let {
                            val commentRequest = CommentRequest(content, uid, slug)
                            val response = MarsApi.retrofitService.postComment(commentRequest)
                            _comment.emit(Resource.Success(response))
                            Log.d("addComment", "Response: $response")
                        }
                    }catch (e: Exception){
                        viewModelScope.launch {
                            _comment.emit(Resource.Error(e.message.toString()))
                        }
                        Log.d("addComment", "Error: ${e.message}")
                    }
                }
            }catch (e: Exception){
                viewModelScope.launch {
                    _comment.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    fun addFavFilm(slug: String) {
        viewModelScope.launch {
            try {
                val username = Constants.getUsername(context)
                username?.let {
                    val favoriteRequest = FavoriteRequest(username, slug)
                    val response = MarsApi.retrofitService.addFavFilm(favoriteRequest)
                    Log.d("addFavFilm", "Response: $response")
                }
            } catch (e: Exception) {
                Log.e("addFavFilm", "Error adding favorite: ${e.message}")
            }
        }
    }

    fun deleteFavFilm(slug: String) {
        viewModelScope.launch {
            try {
                // Gửi yêu cầu xóa phim
                val username = Constants.getUsername(context)
                username?.let {
                    username.let {
                        val response = MarsApi.retrofitService.deleteFavFilm(username, slug)
                        Log.d("addFavFilm", "Response: $response")
                    }
                    // Sau khi xóa thành công, tải lại danh sách yêu thích
                }
            } catch (e: Exception) {
                Log.e("deleteFavFilm", "Error deleting favorite: ${e.message}")
            }
        }
    }



    private fun getFilmSameCategory(category: String) {
        viewModelScope.launch {
            _sameCategory.emit(Resource.Loading())
        }
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmsByCategory(category)
                _sameCategory.emit(Resource.Success(response))
                Log.d("getFilmSameCategory", "${response.status}")
            } catch (e: Exception) {
                _sameCategory.emit(Resource.Error(e.message.toString()))
                Log.d("getFilmSameCategory", "error: ${e.message}")
            }
        }
    }

    fun getFilmDetailBySlug(slug: String) {

        viewModelScope.launch {
            _filmDetail.emit(Resource.Loading())
        }

        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmDetail(slug, Constants.getUsername(context))
                _filmDetail.emit(Resource.Success(response))
                Log.d("getFilmDetailBySlug", "${response}")

                val firstEpisode = response.episodes.firstOrNull()
                Log.d("firstEpisode", "firstEpisode: ${firstEpisode?.name}")

                val category = response.categories.firstOrNull()?.name.toString()
                getFilmSameCategory(category)

                val listComment = response.comments
                _listComment.emit(Resource.Success(listComment))

                firstEpisode?.let {
                    _videoUrl.emit(Resource.Success(it.linkM3u8))
                    Log.d("link", "link: ${it.linkM3u8}")
                }

                Log.d("getMovieDetailBySlug", response.name)


            } catch (e: Exception) {
                _filmDetail.emit(Resource.Error(e.message.toString()))
                Log.d("getMovieDetailBySlug", e.message.toString())
            }
        }
    }

    fun deleteComment(id: Int, currentUserName: String) {
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.removeComment(id, currentUserName)
                Log.d("deleteComment", "API Response: $response")
            } catch (e: Exception) {
                Log.d("deleteComment", e.message.toString())
            }
        }
    }






}