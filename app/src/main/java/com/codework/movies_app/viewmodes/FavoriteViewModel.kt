package com.codework.movies_app.viewmodes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.User
import com.codework.movies_app.network.MarsApi
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
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _status = MutableStateFlow<Resource<ApiResponse>>(Resource.Unspecified())
    val status = _status.asStateFlow()

    init {
        val username = Constants.getUsername(context)
        if (!username.isNullOrEmpty()) {
            getFavFilm(username)
        } else {
            viewModelScope.launch {
                _status.emit(Resource.Error("Vui lòng đăng nhập để xem danh sách yêu thích."))
            }
        }
    }

    fun deleteFavFilm(username: String, slug: String){
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.deleteFavFilm(username, slug)
                Log.d("deleteFavFilm", response)
            }catch (e: Exception){
                Log.d("deleteFavFilm", e.message.toString())
            }
        }
    }

    fun getFavFilm(username: String) {
        viewModelScope.launch {
            _status.emit(Resource.Loading())
            try {
                val response = MarsApi.retrofitService.getListOfFavFilm(username)
                _status.emit(Resource.Success(response))
                Log.d("getFavFilm", response.items.toString())
            } catch (e: Exception) {
                _status.emit(Resource.Error(e.message.toString()))
                Log.d("getFavFilm", e.message.toString())
            }
        }
    }
}

