package com.sun.camarecrop

import android.util.Log
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * Created by nguyenxuanhoi on 2019-09-06.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class ViewModelUpdate {
    fun uploadImageQuestion(imagePath: String): Single<UUID> = Single.create {
        val file = File(imagePath)
        val requestBody = RequestBody.create(null, file)
        val part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        Client.service.uploadImageQuestion(part).subscribe({
            Log.d("TAG", "SUCCESS")
        }, {
            Log.d("TAG", "SUCCESS")
        })
    }
}