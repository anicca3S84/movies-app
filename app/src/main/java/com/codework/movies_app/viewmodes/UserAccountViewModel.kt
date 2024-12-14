package com.codework.movies_app.viewmodes

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.User
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.utils.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        firestore.collection("users").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateUser(user: User) {
        val emailValidation = validateEmail(user.email)
        val usernameValidation = validateUsername(user.username)
        val phoneValidation = validatePhone(user.phone)
        val genderValidation = validateGender(user.gender)

        if (emailValidation is ValidationInfo.Error ||
            usernameValidation is ValidationInfo.Error ||
            phoneValidation is ValidationInfo.Error ||
            genderValidation is ValidationInfo.Error
        ) {
            val errorMessage = listOfNotNull(
                (emailValidation as? ValidationInfo.Error)?.message,
                (usernameValidation as? ValidationInfo.Error)?.message,
                (phoneValidation as? ValidationInfo.Error)?.message,
                (genderValidation as? ValidationInfo.Error)?.message
            ).joinToString("\n")

            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(errorMessage))
            }
            return
        }

        // Nếu tất cả đều hợp lệ, tiến hành lưu
        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
            firestore.collection("users").document(auth.uid!!).set(user)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _updateInfo.emit(Resource.Success(user))
                        try {
                            val response = MarsApi.retrofitService.updateUser(auth.uid!!, user.email, user.username)

                            Log.d("_updateInfo", "Success: $response")
                        }catch (e: Exception) {
                            Log.e("_updateInfo", "Error: ${e.message}")
                        }
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _updateInfo.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun validateUsername(username: String): ValidationInfo {
        return if (username.isBlank()) {
            ValidationInfo.Error("Username must not be empty.")
        } else {
            ValidationInfo.Success
        }
    }

    private fun validatePhone(phone: String): ValidationInfo {
        return if (phone.isBlank()) {
            ValidationInfo.Error("Phone number must not be empty.")
        } else if (!phone.matches(Regex("^\\+?[0-9]{10,15}$"))) {
            ValidationInfo.Error("Invalid phone number format.")
        } else {
            ValidationInfo.Success
        }
    }

    private fun validateGender(gender: String): ValidationInfo {
        return if (gender.isBlank()) {
            ValidationInfo.Error("Gender must not be empty.")
        } else {
            ValidationInfo.Success
        }
    }



}
