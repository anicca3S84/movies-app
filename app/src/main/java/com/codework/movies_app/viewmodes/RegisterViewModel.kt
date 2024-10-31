package com.codework.movies_app.viewmodes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.utils.RegisterFieldState
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.codework.movies_app.data.User
import com.codework.movies_app.utils.Constants.USER_COLLECTION
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.utils.validateConfirmPassword
import com.codework.movies_app.utils.validateEmail
import com.codework.movies_app.utils.validatePassword
import com.codework.movies_app.utils.validateUserName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register = _register.asStateFlow()

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun register(user: User, username: String, confirmPassword: String) {
        if (checkValidation(user, username, confirmPassword)) {
            viewModelScope.launch {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _register.emit(Resource.Error(it.message.toString()))
                    }
                }

        } else {
            val registerFieldsState = RegisterFieldState(
                validateEmail(user.email),
                validateUserName(username),
                validatePassword(user.password),
                validateConfirmPassword(user.password, confirmPassword)
            )
            viewModelScope.launch {
                _validation.send(registerFieldsState)
            }
        }

    }

    private fun saveUserInfo(uid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _register.emit(Resource.Success(user))
                    Log.d("Save", "Success")
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _register.emit(Resource.Error(it.message.toString()))
                    Log.d("Save", "Failed")
                }
            }
    }

    private fun checkValidation(user: User, username: String, confirmPassword: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val usernameValidation = validateUserName(username)
        val passwordValidation = validatePassword(user.password)
        val confirmPasswordValidation = validateConfirmPassword(user.password, confirmPassword)
        val shouldRegister = emailValidation is ValidationInfo.Success
                && passwordValidation is ValidationInfo.Success
                && confirmPasswordValidation is ValidationInfo.Success
                && usernameValidation is ValidationInfo.Success
        return shouldRegister
    }
}