package com.codework.movies_app.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.codework.movies_app.R
import com.codework.movies_app.activities.LoginRegisterActivity
import com.codework.movies_app.fragments.login_register.LoginFragment

fun showLoginDialog(context: Context, navController: NavController) {
    AlertDialog.Builder(context)
        .setTitle("Yêu cầu đăng nhập")
        .setMessage("Bạn cần đăng nhập để thực hiện thao tác này. Bạn có muốn đăng nhập không?")
        .setPositiveButton("Đăng nhập") { _, _ ->
            val intent = Intent(context, LoginRegisterActivity::class.java)
            context.startActivity(intent)
        }
        .setNegativeButton("Hủy", null)
        .show()
}
