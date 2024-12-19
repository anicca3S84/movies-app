package com.codework.movies_app.dialogs

import android.app.AlertDialog
import android.content.Context

fun deleteHistoryDialog(
    context: Context,
    onConfirm: () -> Unit
) {
    AlertDialog.Builder(context)
        .setTitle("Xác nhận")
        .setMessage("Bạn có chắc chắn muốn xóa phim này?")
        .setPositiveButton("Xóa") { _, _ ->
            onConfirm.invoke()
        }
        .setNegativeButton("Hủy", null)
        .show()
}