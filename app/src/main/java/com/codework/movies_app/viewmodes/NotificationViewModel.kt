package com.codework.movies_app.viewmodes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.Notification
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Constants
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
        fetchNotifications()
    }

    private fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    private fun fetchNotifications() {
        val userId = getUserId()
        if (userId != null) {
            viewModelScope.launch {
                _notification.emit(Resource.Loading())
                try {
                    val response = MarsApi.retrofitService.getNotification(userId)
                    _notification.emit(Resource.Success(response))
                } catch (e: Exception) {
                    _notification.emit(Resource.Error(e.message ?: "Error fetching notifications"))
                }
            }
        } else {
            viewModelScope.launch {
                _notification.emit(Resource.Error("User not logged in"))
            }
        }
    }

    fun refreshNotifications() {
        fetchNotifications()
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            try {
                MarsApi.retrofitService.deleteNotification(id)
                refreshNotifications()
            } catch (e: Exception) {
                Log.e("deleteNotification", "Error: ${e.message}")
            }
        }
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch {
            try {
                MarsApi.retrofitService.markNotificationAsRead(id)
            } catch (e: Exception) {
                Log.e("markNotificationAsRead", "Error: ${e.message}" )
            }
        }
    }
}
