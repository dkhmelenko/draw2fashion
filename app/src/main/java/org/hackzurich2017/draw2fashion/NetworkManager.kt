package org.hackzurich2017.draw2fashion

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object NetworkManager {

    private val api: RecognitionApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        api = retrofit.create(RecognitionApi::class.java)
    }

    interface RecognitionApi {

        @GET("/top")
        fun getTop(@Query("limit") limit: String): Observable<FashwellData>
    }
}