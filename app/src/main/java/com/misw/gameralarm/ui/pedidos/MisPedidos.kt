package com.misw.gameralarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

        ApiClient.apiService.listarPedidos().enqueue(object : Callback<List<PedidoResponse>> {
            override fun onResponse(call: Call<List<PedidoResponse>>, response: Response<List<PedidoResponse>>) {
                if (response.isSuccessful) {
                    val ordenes = response.body() ?: emptyList()
                    ordersList.removeAllViews()
                    ordenes.forEach { orden ->
                        val cardView = layoutInflater.inflate(R.layout.order_card_view, null) as CardView
                        val orderNumberTextView = cardView.findViewById<TextView>(R.id.orderNumberTextView)
                        val itemCountTextView = cardView.findViewById<TextView>(R.id.itemCountTextView)

                        orderNumberTextView.text = "Order #${orden.pedido_id}"
                        itemCountTextView.text = "${orden.total} items"

                        cardView.setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("orderId", orden.pedido_id.toString())
                            }
                        }

                        ordersList.addView(cardView)
                    }
                } else {
                    showError("Error al obtener pedidos")
                }
            }

            override fun onFailure(call: Call<List<PedidoResponse>>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })

        return view
    }

    private fun showError(message: String) {
        // Aquí podrías agregar un Toast, Log o Dialog
    }
}
