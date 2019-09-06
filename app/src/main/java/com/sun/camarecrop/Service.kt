package com.sun.camarecrop
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Service {
    companion object {
        private const val APP_VERSION = "/app-version"
    }

    @Multipart
    @POST("$APP_VERSION/upload-image-question")
    fun uploadImageQuestion(@Part part: MultipartBody.Part): Single<Any>


}