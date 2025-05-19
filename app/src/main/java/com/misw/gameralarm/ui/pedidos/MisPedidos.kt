package com.misw.gameralarm

import OrderResponse
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisPedidos : Fragment() {

    private lateinit var ordersList: LinearLayout
    private lateinit var sharedPref: android.content.SharedPreferences
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false)
        ordersList = view.findViewById(R.id.ordersList)
        sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)

        view.findViewById<ImageView>(R.id.closeButton).setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }

        val userId = sharedPref.getString("user_id", null)?.toIntOrNull()
        val userRole = sharedPref.getString("user_role", null)
        token = sharedPref.getString("auth_token", null)

        if (userId == null || userRole == null) {
            showError("Datos de usuario incompletos")
            return view
        }

        loadOrders(userId, userRole)

        return view
    }

    private fun loadOrders(userId: Int, role: String) {
        val call = when (role) {
            "CLIENTE" -> ApiClient.apiService.listarPedidosPorUsuario("Bearer $token",userId)
            else -> ApiClient.apiService.listarPedidosPorVendedor("Bearer $token",userId)
        }

        call.enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(
                call: Call<List<OrderResponse>>,
                response: Response<List<OrderResponse>>
            ) {
                if (response.isSuccessful) {
                    val orders = response.body().orEmpty()
                    showOrders(orders)
                } else {
                    showError("Error al obtener pedidos")
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })
    }

    private fun showOrders(orders: List<OrderResponse>) {
        ordersList.removeAllViews()

        if (orders.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No tienes pedidos todavía."
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            ordersList.addView(emptyText)
            return
        }

        orders.forEach { order ->
            val card = createOrderCard(order)
            ordersList.addView(card)
        }
    }

    private fun createOrderCard(order: OrderResponse): View {
        val cardView = layoutInflater.inflate(
            R.layout.order_card_view, ordersList, false
        ) as CardView

        cardView.findViewById<TextView>(R.id.orderNumberTextView).text = "Pedido #${order.pedido_id}"
        cardView.findViewById<TextView>(R.id.itemCountTextView).text = "${order.total} COP"

        cardView.setOnClickListener {
            sharedPref.edit().putString("pedido_id", order.pedido_id.toString()).apply()
            val bundle = Bundle().apply { putString("orderId", order.pedido_id.toString()) }
            findNavController().navigate(R.id.action_misPedidos_to_detallePedido, bundle)
        }

        return cardView
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
