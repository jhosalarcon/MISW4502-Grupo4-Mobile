package com.misw.gameralarm.data.network

import com.misw.gameralarm.data.model.ClienteResponse
import com.misw.gameralarm.data.model.CrearCuentaRequest
import com.misw.gameralarm.data.model.CrearCuentaResponse
import com.misw.gameralarm.data.model.LoginRequest
import com.misw.gameralarm.data.model.LoginResponse
import com.misw.gameralarm.data.model.NuevoProductoRequest
import com.misw.gameralarm.data.model.NuevoProductoResponse
import com.misw.gameralarm.data.model.PedidoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/register")
    fun registerUser(@Body request: CrearCuentaRequest): Call<CrearCuentaResponse>

    @POST("auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("inventary/products")
    fun agregarProducto(@Header("Authorization") token: String, @Body request: NuevoProductoRequest): Call<NuevoProductoResponse>

    @GET("sales/sales")
    fun listarPedidos(): Call<List<PedidoResponse>>

    @GET("inventary/products")
    fun obtenerProductos(@Header("Authorization") token: String): Call<List<NuevoProductoRequest>>

    @GET("inventary/products/{id}")
    fun obtenerProducto(@Path("id") id: Int): Call<NuevoProductoResponse>

    @GET("/auth/clients/all")
    fun listarClientes(@Header("Authorization") token: String): Call<List<ClienteResponse>>

}
