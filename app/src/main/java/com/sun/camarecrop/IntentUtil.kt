package com.sun.camarecrop

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment

object IntentUtil {
    fun openCameraApp(fragment: Fragment, imageUri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }

        fragment.startActivityForResult(intent, PermissionUtil.CAMERA_APP)
    }

    fun openCopperApp(fragment: Fragment, imageUri: Uri) {
        val intent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(imageUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            putExtra("aspectX", 8)
            putExtra("aspectY", 9)
            putExtra("scaleUpIfNeeded", true)
            putExtra("scale", "true")
            putExtra("return-data", false)
            putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name)
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        fragment.startActivityForResult(intent, PermissionUtil.COPPER_APP)
    }

    fun openGalleryApp(fragment: Fragment, allowMultiple: Boolean = false) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }

        fragment.startActivityForResult(intent, PermissionUtil.GALLERY_APP)
    }

}