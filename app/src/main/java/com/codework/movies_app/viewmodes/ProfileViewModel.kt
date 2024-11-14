package com.codework.movies_app.viewmodes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.User
import com.codework.movies_app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){
    private val _checkLogin = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val checkLogin = _checkLogin.asStateFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        val userId = auth.uid
        if(userId != null){
            Log.d("userId", userId.toString())
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if(document.exists()){
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
                    }else{
                        viewModelScope.launch {
                            _user.emit(Resource.Error("Không tìm thấy người dùng"))
                        }
                    }
                }
                .addOnFailureListener{
                    viewModelScope.launch {
                        _user.emit(Resource.Error("Vui lòng đăng nhập"))
                    }
                }
        }else{
            viewModelScope.launch {
                _user.emit(Resource.Error("Vui lòng đăng nhập!"))
            }
        }

    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if(currentUser != null){
            viewModelScope.launch {
                _checkLogin.emit(Resource.Success(currentUser))
                getUser()
            }
        }else{
            viewModelScope.launch {
                _checkLogin.emit(Resource.Error("Vui lòng đăng nhập!"))
            }
        }
    }



    fun logout(){
        if(auth.currentUser != null)
            auth.signOut()
        Log.d("Logout","success")
    }
}