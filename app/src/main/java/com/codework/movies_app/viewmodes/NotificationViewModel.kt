package com.codework.movies_app.viewmodes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.Notification
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _notification = MutableStateFlow<Resource<List<Notification>>>(Resource.Unspecified())
    val notification = _notification.asStateFlow()

    init {
        getUserIdAndFetchNotifications()
    }

    private fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    private fun getUserIdAndFetchNotifications() {
        val userId = getUserId()
        if (userId != null) {
            getNotification(userId)
        } else {
            viewModelScope.launch {
                _notification.emit(Resource.Error("User not logged in"))
            }
        }
    }

    private fun getNotification(userId: String) {
        viewModelScope.launch {
            _notification.emit(Resource.Loading())
        }
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getNotification(userId)
                Log.d("getNotification", "Response: $response")
                _notification.emit(Resource.Success(response))
            } catch (e: Exception) {
                _notification.emit(Resource.Error(e.message.toString()))
                Log.d("getNotification", e.message.toString())
            }
        }
    }
}
