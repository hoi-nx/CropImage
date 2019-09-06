package com.sun.camarecrop

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object PermissionUtil {
    val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun isGrantResultsGranted(grantResults: IntArray): Boolean {
        if (grantResults.isNotEmpty()) {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }

    fun isPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        permissions.forEach {
            if (!isPermissionGranted(context, it)) {
                return false
            }
        }
        return true
    }

    fun requestStoragePermission(fragment: Fragment) {
        if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            || fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            fragment.activity!!.showMessageDialog(R.string.alert_permissions_rationale) {
                fragment.requestPermissions(storagePermissions, PERMISSION_STORAGE)
            }
        } else {
            fragment.requestPermissions(storagePermissions, PERMISSION_STORAGE)
        }
    }
    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun FragmentActivity.showMessageDialog(
        message: String,
        cancelable: Boolean = false,
        onPositiveButtonClicked: () -> Unit = {}
    ) {
        AlertDialogUtil.showMessageDialog(this, message, cancelable, onPositiveButtonClicked)
    }

    private fun FragmentActivity.showMessageDialog(
        @StringRes messageId: Int,
        cancelable: Boolean = false,
        onPositiveButtonClicked: () -> Unit = {}
    ) {
        val message = getString(messageId)
        showMessageDialog(message, cancelable, onPositiveButtonClicked)
    }

    const val PERMISSION_STORAGE = 1111

    const val CAMERA_APP = 2222
    const val COPPER_APP = 2223
    const val GALLERY_APP = 2224

}