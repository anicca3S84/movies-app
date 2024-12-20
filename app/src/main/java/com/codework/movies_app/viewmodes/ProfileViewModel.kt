package com.codework.movies_app.viewmodes

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.activities.LoginRegisterActivity
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.User
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _checkLogin = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val checkLogin = _checkLogin.asStateFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _history = MutableStateFlow<Resource<ApiResponse>>(Resource.Unspecified())
    val history = _history.asStateFlow()

    private val _deleteAllHistory = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val deleteAllHistory = _deleteAllHistory.asStateFlow()

    init {
        checkUserStatus()
        Constants.getUsername(context)?.let { getHistory(it) }
    }

    fun getHistory(username: String?) {
        if (username.isNullOrEmpty()) {
            viewModelScope.launch {
                _history.emit(Resource.Success(ApiResponse()))
            }
            return
        }

        viewModelScope.launch {
            _history.emit(Resource.Loading())
            try {
                val response = MarsApi.retrofitService.getHistory(username)
                _history.emit(Resource.Success(response))
                Log.d("getHistory", "${response.items}")
            } catch (e: Exception) {
                _history.emit(Resource.Error(e.message.toString()))
                Log.d("getHistory", "error: ${e.message}")
            }
        }
    }

    fun deleteHistory(username: String, filmId: Int){
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.deleteHistory(username, filmId)
                Log.d("deleteFavFilm", response)
            }catch (e: Exception){
                Log.d("deleteFavFilm", e.message.toString())
            }
        }
    }

    fun deleteAllHistory(username: String) {
        viewModelScope.launch {
            try {
                _deleteAllHistory.emit(Resource.Loading()) // Emit loading state
                val response = MarsApi.retrofitService.deleteAllHistory(username)
                Log.d("deleteAllHistory", response)

                // After deletion, emit an empty list as success
                _deleteAllHistory.emit(Resource.Success(response))
            } catch (e: Exception) {
                Log.e("deleteAllHistory", e.message.toString())
                _deleteAllHistory.emit(Resource.Error(e.message.toString()))
            }
        }
    }



    private fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        val userId = auth.uid
        if (userId != null) {
            Log.d("userId", userId.toString())
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            viewModelScope.launch {
                                _user.emit(Resource.Success(user))
                            }
                        } ?: run {
                            viewModelScope.launch {
                                _user.emit(Resource.Error("Không tìm thấy người dùng"))
                            }
                        }
                    } else {
                        viewModelScope.launch {
                            _user.emit(Resource.Error("Không tìm thấy người dùng"))
                        }
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _user.emit(Resource.Error("Vui lòng đăng nhập"))
                    }
                }
        } else {
            viewModelScope.launch {
                _user.emit(Resource.Error("Vui lòng đăng nhập!"))
            }
        }

    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                _checkLogin.emit(Resource.Success(currentUser))
                getUser()
            }
        } else {
            viewModelScope.launch {
                _checkLogin.emit(Resource.Error("Vui lòng đăng nhập!"))
            }
        }
    }


    fun logout() {
        if (auth.currentUser != null) {
            Log.d("Logout", "User before sign out: ${auth.currentUser?.uid}")

            removeUsernameFromSharedPreferences()

            val refreshedUsername = Constants.getUsername(context)
            Log.d("Logout", "Username sau khi làm mới: $refreshedUsername")

            auth.signOut()
        }
    }

    private fun removeUsernameFromSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.remove("username") // Xóa đúng KEY
        editor.apply()

        // Log kiểm tra sau khi xóa
        val username = sharedPreferences.getString("username", null)
        Log.d("SharedPreferences", "Username sau khi xóa: $username")
    }



}