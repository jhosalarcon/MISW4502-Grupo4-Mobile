package com.misw.gameralarm

import OrderResponse
import DetallePedidoRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetallePedido : Fragment() {

    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_pedido, container, false)
        val ordersList: LinearLayout = view.findViewById(R.id.ordersList)
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val estadoPedidoTextView = view.findViewById<TextView>(R.id.estadoPedidoTextView)
        val totalPedidoTextView = view.findViewById<TextView>(R.id.totalPedidoTextView)

        closeButton.setOnClickListener { findNavController().navigateUp() }
        btnBack.setOnClickListener { findNavController().navigateUp() }

        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        token = sharedPref.getString("auth_token", null)
        val pedidoId = sharedPref.getString("pedido_id", null)
        val orderNumberTextView = view.findViewById<TextView>(R.id.orderNumberTextView)
        orderNumberTextView.text = "Pedido #$pedidoId"


        if (pedidoId == null) {
            showError("No se encontró el ID del pedido")
            return view
        }

        ApiClient.apiService.listarProductosPorOrden("Bearer $token", pedidoId.toInt()).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    val pedido = response.body()
                    estadoPedidoTextView.text = "Estado: ${pedido?.estado ?: "Desconocido"}"
                    totalPedidoTextView.text = "Total: ${pedido?.total ?: 0.0} COP"

                    val productos = pedido?.detalles ?: emptyList()
                    for (producto in productos) {
                        val itemView = inflater.inflate(R.layout.item_producto, ordersList, false)

                        val nombre = itemView.findViewById<TextView>(R.id.nombreProducto)
                        val cantidad = itemView.findViewById<TextView>(R.id.cantidadProducto)
                        val precio = itemView.findViewById<TextView>(R.id.precioProducto)

                        nombre.text = producto.id_producto.toString()
                        cantidad.text = "Cantidad: ${producto.cantidad}"
                        precio.text = "Precio: ${producto.precio_unitario} COP"

                        ordersList.addView(itemView)
                    }
                } else {
                    showError("No se pudo obtener el detalle del pedido")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })

        return view
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
