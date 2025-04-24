package com.misw.gameralarm.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val nombre: String,
    val rol: String,
    val token: String

)