package com.codework.movies_app.firebase

import android.util.Log
import com.codework.movies_app.data.User
import com.codework.movies_app.utils.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

class FirebaseCommon(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun getCurrentUser(uid: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        firebaseAuth.currentUser?.let {
            firestore.collection(USER_COLLECTION)
                .document(it.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    onSuccess(user)  // Gọi callback onSuccess khi dữ liệu được lấy thành công
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)  // Gọi callback onFailure khi có lỗi
                }
        } ?: run {
            onFailure(Exception("User is not logged in"))
        }
    }
}
