package org.hackzurich2017.draw2fashion

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.hackzurich2017.draw2fashion.fashionwell.AttributesResponse
import org.hackzurich2017.draw2fashion.fashionwell.ProductsResponse
import java.io.File

object FashwellManager {

    fun getFileAttributes(file: File): Observable<AttributesResponse> {
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.getName(), reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "image")
        return NetworkManager.api.getFileAttributes(body, name)
    }

    fun postFile(file: File): Observable<ProductsResponse> {
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.getName(), reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "image")

        return NetworkManager.api.uploadFile(body, name);
    }
}