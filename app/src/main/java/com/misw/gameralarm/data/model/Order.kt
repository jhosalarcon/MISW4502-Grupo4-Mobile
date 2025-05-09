import com.misw.gameralarm.DetallePedido

data class OrderRequest(
    val id_cliente: Int,
    val id_vendedor: Int,
    val detalles: List<DetallePedidoRequest>
)

data class DetallePedidoRequest(
    val id_producto: Int,
    val cantidad: Int,
    val precio_unitario: Double
)

data class OrderResponse(
    val detalles: List<DetallePedidoRequest>,
    val estado: String,
    val fecha_creacion: String,
    val id_cliente: Int,
    val id_vendedor: Int,
    val pedido_id: Int,
    val total: Double
)