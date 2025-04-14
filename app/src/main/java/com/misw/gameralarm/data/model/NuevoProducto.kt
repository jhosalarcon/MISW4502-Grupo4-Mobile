package com.misw.gameralarm.data.model

data class NuevoProductoRequest(
    val nombre: String,
    val descripcion: String,
    val precio_unitario: Double,
    val cantidad: Int,
    val tipo: String,
    val ubicacion: String
)

data class NuevoProductoResponse(
    val producto_id: Int,
    val nombre: String,
    val descripcion: String,
    val precio_unitario: Double,
    val cantidad: Int,
    val tipo: String,
    val ubicacion: String,
    val creado_en: String
)