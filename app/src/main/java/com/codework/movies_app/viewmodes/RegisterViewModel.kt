package com.codework.movies_app.viewmodes

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.utils.RegisterFieldState
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.codework.movies_app.data.User
import com.codework.movies_app.dto.UserDto
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Constants.USER_COLLECTION
import com.codework.movies_app.utils.Constants.saveUsername
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.utils.validateConfirmPassword
import com.codework.movies_app.utils.validateEmail
import com.codework.movies_app.utils.validatePassword
import com.codework.movies_app.utils.validateUserName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register = _register.asStateFlow()

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()


    fun register(user: User, username: String, confirmPassword: String) {
        Log.d("register","go hear")
        if (checkValidation(user, username, confirmPassword)) {
            viewModelScope.launch {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        Log.d("createUserWithEmailAndPassword", "Success")
                        saveUserInfo(it.uid, user)

                    }
                }
                .addOnFailureListener {
                    Log.d("createUserWithEmailAndPassword", "Failed")
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

    private fun getUserData(uid: String) {
        db.collection(USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d("getUserData", "Success")
                val user = document.toObject(User::class.java)
                if(user != null){
                    postUserToApi(user, uid)
                }else{
                    viewModelScope.launch {
                        _register.emit(Resource.Error("User not found"))
                    }
                    Log.d("getUserData", "Failed")

                }
            }
            .addOnFailureListener { ex ->
                viewModelScope.launch {
                    _register.emit(Resource.Error(ex.message.toString()))
                }
            }
    }

    private fun postUserToApi(user: User, uid: String) {
        Log.d("postUserToApi", "Go here")
        viewModelScope.launch {
            try {
                val userDto = UserDto(uid, user.email, user.username,"")
                val response = MarsApi.retrofitService.insertUser(userDto)
                Log.d("postUserToApi", "Success\n${response.uid} ${response.email}, ${response.userName}")
            }catch (e: Exception){
                Log.d("postUserToApi", e.message.toString())
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
                getUserData(uid)
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