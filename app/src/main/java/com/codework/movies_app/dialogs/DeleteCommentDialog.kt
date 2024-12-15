package com.codework.movies_app.dialogs

import android.app.AlertDialog
import android.content.Context

fun deleteCommentDialog(
    context: Context,
    onConfirm: () -> Unit // Callback for delete confirmation
) {
    AlertDialog.Builder(context)
        .setTitle("Xác nhận")
        .setMessage("Bạn có chắc chắn muốn xóa bình luận này?")
        .setPositiveButton("Xóa") { _, _ ->
            onConfirm.invoke() // Call the confirmation callback
        }
        .setNegativeButton("Hủy", null)
        .show()
}
