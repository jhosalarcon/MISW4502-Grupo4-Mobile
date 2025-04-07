package com.misw.gameralarm.data.network

import com.misw.gameralarm.data.model.CrearCuentaRequest
import com.misw.gameralarm.data.model.CrearCuentaResponse
import com.misw.gameralarm.data.model.LoginRequest
import com.misw.gameralarm.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    fun registerUser(@Body request: CrearCuentaRequest): Call<CrearCuentaResponse>

    @POST("auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}
