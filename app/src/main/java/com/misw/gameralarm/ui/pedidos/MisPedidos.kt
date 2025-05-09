package com.misw.gameralarm

import OrderResponse
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
import com.misw.gameralarm.data.model.PedidoResponse
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisPedidos : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false)
        val ordersList: LinearLayout = view.findViewById(R.id.ordersList)
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        val userId = sharedPref.getString("user_id", null)?.toIntOrNull()

        if (userId == null) {
            showError("Usuario no identificado")
            return view
        }


        ApiClient.apiService.listarPedidosPorUsuario(userId).enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                if (response.isSuccessful) {
                    val ordenes = response.body() ?: emptyList()
                    ordersList.removeAllViews()

                    if (ordenes.isEmpty()) {
                        val emptyText = TextView(requireContext()).apply {
                            text = "No tienes pedidos todavía."
                            textSize = 16f
                            setPadding(16, 16, 16, 16)
                        }
                        ordersList.addView(emptyText)
                    } else {
                        ordenes.forEach { orden ->
                            val cardView = layoutInflater.inflate(R.layout.order_card_view, ordersList, false) as CardView
                            val orderNumberTextView = cardView.findViewById<TextView>(R.id.orderNumberTextView)
                            val itemCountTextView = cardView.findViewById<TextView>(R.id.itemCountTextView)

                            orderNumberTextView.text = "Pedido #${orden.pedido_id}"
                            itemCountTextView.text = "${orden.total} COP"

                            cardView.setOnClickListener {
                                val pedidoId = orden.pedido_id.toString()

                                val editor = sharedPref.edit()
                                editor.putString("pedido_id", pedidoId)
                                editor.apply()

                                val bundle = Bundle().apply {
                                    putString("orderId", pedidoId)
                                }
                                findNavController().navigate(R.id.action_misPedidos_to_detallePedido, bundle)
                            }

                            ordersList.addView(cardView)
                        }
                    }
                } else {
                    showError("Error al obtener pedidos")
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })


        return view
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
