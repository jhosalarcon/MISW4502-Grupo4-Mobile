package com.misw.gameralarm.data.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String? = null,
    val token: String

)