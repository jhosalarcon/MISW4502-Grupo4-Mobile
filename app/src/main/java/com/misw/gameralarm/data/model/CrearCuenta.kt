package com.misw.gameralarm.data.model

data class CrearCuentaRequest(
    val nombre: String,
    val telefono: String,
    val email: String,
    val password: String,
    val rol: String
)

data class CrearCuentaResponse(
    val message: String,
    val token: String
)