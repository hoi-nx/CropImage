package com.sun.camarecrop

import android.content.Context
import androidx.appcompat.app.AlertDialog

object AlertDialogUtil {
    fun showMessageDialog(context: Context,
                          message: String,
                          cancelable: Boolean,
                          onPositiveButtonClicked: () -> Unit) {
        val dialog = AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setMessage(message)
                .setPositiveButton(R.string.all_ok) { dialog, _ ->
                    onPositiveButtonClicked.invoke()
                    dialog.dismiss()
                }
                .create()
        dialog.show()
    }

}