package com.codework.movies_app.viewmodes

import android.content.Context
import android.content.SharedPreferences
import android.net.http.HttpException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.User
import com.codework.movies_app.dto.UserDto
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.request.SaveTokenRequest
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Constants.INTRODUCTION_SP
import com.codework.movies_app.utils.Constants.USER_COLLECTION
import com.codework.movies_app.utils.Constants.saveUsername
import com.codework.movies_app.utils.LoginFieldState
import com.codework.movies_app.utils.RegisterFieldState
import com.codework.movies_app.utils.Resource
import com.codework.movies_app.utils.ValidationInfo
import com.codework.movies_app.utils.validateConfirmPassword
import com.codework.movies_app.utils.validateEmail
import com.codework.movies_app.utils.validatePassword
import com.codework.movies_app.utils.validateUserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val fireStore: FirebaseFirestore
) : ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _validation = Channel<LoginFieldState>()
    val validation = _validation.receiveAsFlow()


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        if (checkValidation(email, password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                //it ở cấp độ của addOnSuccessListener là AuthResult
                //it trong let sẽ là một FirebaseUser
                .addOnSuccessListener { authResult ->
                    val firebaseUser = authResult.user

                    if (firebaseUser != null) {

                        viewModelScope.launch {

                            val userName = getUserNameById()
                            if (userName != null) {
                                saveUsername(context, userName)
                                Log.d("userName in launch viewModel: ", userName)
                                try {
                                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                                    Log.d("FCMToken", "FCM Token: $fcmToken")
                                    pushTokenToApi(fcmToken)
                                } catch (tokenError: Exception) {
                                    Log.e("FCMToken", "Error retrieving FCM token: ${tokenError.message}")
                                }
                            } else {
                                Log.d("userName in launch viewModel: ", "null")
                            }
//                            FirebaseMessaging.getInstance().token
//                                .addOnSuccessListener { fcmToken ->
//                                    Log.d("FCMToken", "FCM Token: $fcmToken")
//                                    viewModelScope.launch {
//                                        pushTokenToApi(fcmToken)
//                                    }
//
//                                }
//                                .addOnFailureListener { tokenError ->
//                                    Log.e("FCMToken", "Error retrieving FCM token: ${tokenError.message}")
//                                }
                            _login.emit(Resource.Success(firebaseUser))
                            Log.d("LoginViewModel", "User logged in successfully.")
                        }
                    }

//                    firebaseUser?.getIdToken(true)
//                        ?.addOnSuccessListener { result ->
//                            val token = result.token
//
//                        }
//                        ?.addOnFailureListener { tokenError ->
//                            viewModelScope.launch {
//                                _login.emit(Resource.Error("Token error: ${tokenError.message}"))
//                            }
//                        }
                }
        }else{
            val loginFieldsState = LoginFieldState(
                validateEmail(email),
                validatePassword(password)
            )
            viewModelScope.launch {
                _validation.send(loginFieldsState)
            }
        }
    }

    private suspend fun getUserNameById(): String? {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
                try {
                    val document = fireStore.collection(Constants.USER_COLLECTION).document(userId).get().await()
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
//                        user?.let {
//                            val username = it.username
//                            saveUsername(context, username)
//                            Log.d("UserName", "Username: $username")
//                        }
                        return user?.username
                    } else {
                        Log.d("getUserNameById", "User not found!")
                    }
                } catch (e: Exception) {
                    Log.e("getUserNameById", "Error occurred: ${e.message}")
                    if (e is CancellationException) {
                        Log.e("getUserNameById", "Job was cancelled")
                    }
                }
            }
         else {
            Log.e("getUserNameById", "User ID is null!")
        }
        return null
    }


    private suspend fun pushTokenToApi(token: String?) {
        if (token != null) {
            Log.d("Token trong pushTokenToApi: ", token)
        } else {
            Log.d("Token trong pushToenToApi: ", "null")
        }
                try {
                    Log.d("Inside viewModelScope:", "Pass")
                    val username = Constants.getUsername(context)!!
                    Log.d("Username pushTokenToApi", username)
                    if (token != null) {
                        Log.d("Token", token)
                    }
                    val saveTokenRequest = token?.let { SaveTokenRequest(username, it) }
                    val response = saveTokenRequest?.let { MarsApi.retrofitService.saveToken(it) }
                    Log.d("pushTokenToApi", "Response: $response")
                } catch (e: HttpException) {
                    Log.e("pushTokenToApi", "HTTP error: ${e.message} ${e.message}")
                } catch (e: IOException) {
                    Log.e("pushTokenToApi", "Network error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("pushTokenToApi", "Unexpected error: ${e.message}")
                }
            }



    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        val shouldLogin = emailValidation is ValidationInfo.Success
                && passwordValidation is ValidationInfo.Success
        return shouldLogin
    }
}