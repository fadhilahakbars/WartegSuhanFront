package com.netlab.suhan.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogCollection {
    fun displayErrorDialog(ctx: Context, msg: String) {
        AlertDialog.Builder(ctx)
            .setTitle("Error")
            .setMessage(msg)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }
}