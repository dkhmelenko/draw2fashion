package org.hackzurich2017.draw2fashion

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.hackzurich2017.draw2fashion.fashionwell.AttributesResponse
import org.hackzurich2017.draw2fashion.fashionwell.ProductsResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object NetworkManager {

    val api: RecognitionApi

    private val token = "7ac2a9d60e05aadee438d6136ac85a33f92efdfc"

    init {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor({ chain ->
            val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Token " + token)
                    .build()
            chain.proceed(newRequest)
        })
                .addInterceptor(interceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://www.fashwell.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        api = retrofit.create(RecognitionApi::class.java)
    }

    interface RecognitionApi {

        @Multipart
        @POST("/api/hackzurich/v1/attributes/")
        fun getFileAttributes(@Part image: MultipartBody.Part, @Part("name") name: RequestBody): Observable<AttributesResponse>

        @Multipart
        @POST("/api/hackzurich/v1/posts/")
        fun uploadFile(@Part image: MultipartBody.Part, @Part("name") name: RequestBody): Observable<ProductsResponse>

        @GET("/api/hackzurich/v1/product/{sku}/")
        fun getDetails(@Query("sku") sku: String): Observable<ProductDetails>
    }
}