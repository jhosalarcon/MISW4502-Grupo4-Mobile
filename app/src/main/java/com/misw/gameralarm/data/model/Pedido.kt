package com.misw.gameralarm.data.model

data class PedidoRequest(
    val estado: String,
    val fecha_creacion: String,
    val id_cliente: Int,
    val id_vendedor: Int,
    val pedido_id: Int,
    val total: Double,
)

data class PedidoResponse(
    val estado: String,
    val fecha_creacion: String,
    val id_cliente: Int,
    val id_vendedor: Int,
    val pedido_id: Int,
    val total: Double,
)