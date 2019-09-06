package com.sun.camarecrop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sun.camarecrop.IntentUtil.openCopperApp
import kotlinx.android.synthetic.main.camera_fragment.*

/**
 * Created by nguyenxuanhoi on 2019-09-06.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class FragmentCamera :Fragment(){
    private var isCameraAppOpenRequest = false
    private var imagePath: String? = null
    private var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camera_fragment,container,false)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PermissionUtil.CAMERA_APP -> {
                if (resultCode == Activity.RESULT_OK) {
                    openCopperApp()
                }
            }

            PermissionUtil.COPPER_APP -> {
                if (resultCode == Activity.RESULT_OK) {
                    FileUtil.resizeImageFile(imagePath!!)
                    bindViewImageQuestion(imagePath!!)

                }
            }

            PermissionUtil.GALLERY_APP -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val file = FileUtil.createTempFile(context!!, data.data!!)
                    imagePath = file.absolutePath
                    imageUri = FileUtil.getUriFromPath(context!!, imagePath!!)
                    openCopperApp()
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCamera.setOnClickListener({
            handleTakePhotoButtonClicked()
        })
        btnGalary.setOnClickListener({
            handleChooseFromGalleryButtonClicked()


        })
    }

    private fun handleTakePhotoButtonClicked() {
        imagePath = FileUtil.genImagePath()
        imageUri = FileUtil.getUriFromPath(context!!, imagePath!!)

        if (isStoragePermissionGranted()) {
            IntentUtil.openCameraApp(this, imageUri!!)
            return
        }
        requestStoragePermission(true)
    }

    private fun handleChooseFromGalleryButtonClicked() {
        if (isStoragePermissionGranted()) {
            IntentUtil.openGalleryApp(this, false)
            return
        }
        requestStoragePermission(false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionUtil.PERMISSION_STORAGE -> {
                if (PermissionUtil.isGrantResultsGranted(grantResults)) {
                    if (isCameraAppOpenRequest) {
                        IntentUtil.openCameraApp(this, imageUri!!)
                    } else {
                        IntentUtil.openGalleryApp(this, false)
                    }
                }
            }
        }
    }
    private fun isStoragePermissionGranted(): Boolean {
        return PermissionUtil.isPermissionsGranted(context!!, PermissionUtil.storagePermissions)
    }
    private fun openCopperApp() {
        try {
            IntentUtil.openCopperApp(this, imageUri!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun bindViewImageQuestion(image: String) {
        Glide.with(context!!)
            .load(image).into(imgAvata)

    }
    private fun requestStoragePermission(isCameraAppOpenRequest: Boolean) {
        this.isCameraAppOpenRequest = isCameraAppOpenRequest
        PermissionUtil.requestStoragePermission(this)
    }
}