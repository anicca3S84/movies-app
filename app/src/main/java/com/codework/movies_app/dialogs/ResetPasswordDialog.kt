package com.codework.movies_app.dialogs

import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.codework.movies_app.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.dialog_reset_password, null)
    view.setBackgroundResource(R.drawable.bg_dialog)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()
    val edEmail = view.findViewById<EditText>(R.id.edEmail)
    val btnSend = view.findViewById<Button>(R.id.btnSend)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)

    btnCancel.setOnClickListener{
        dialog.dismiss()
    }

    btnSend.setOnClickListener{
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    Log.d("setupBottomSheetDialog","Success")

}