package com.misw.gameralarm.network

import com.misw.gameralarm.data.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://34.31.100.70:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
