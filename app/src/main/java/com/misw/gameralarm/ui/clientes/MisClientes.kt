package com.misw.gameralarm

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
import com.misw.gameralarm.data.model.ClienteResponse
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisClientes : Fragment() {

    private lateinit var ordersList: LinearLayout
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_clientes, container, false)

        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        ordersList = view.findViewById(R.id.ordersList)

        // Ahora es seguro usar requireContext()
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        token = sharedPref.getString("auth_token", null)

        // También puedes considerar sobrescribirlo si viene como argumento
        token = arguments?.getString("token") ?: token

        listarClientes()

        return view
    }

    private fun listarClientes() {
        if (token.isNullOrEmpty()) {
            showError("Token no disponible")
            return
        }

        ApiClient.apiService.listarClientes("Bearer $token").enqueue(object : Callback<List<ClienteResponse>> {
            override fun onResponse(call: Call<List<ClienteResponse>>, response: Response<List<ClienteResponse>>) {
                if (response.isSuccessful) {
                    val clientes = response.body() ?: emptyList()
                    ordersList.removeAllViews()
                    clientes.forEach { cliente ->
                        val cardView = layoutInflater.inflate(R.layout.client_card_view, null) as CardView

                        val usuarioIdTextView = cardView.findViewById<TextView>(R.id.usuarioIdTextView)
                        val nombreClienteTextView = cardView.findViewById<TextView>(R.id.nombreClienteTextView)

                        usuarioIdTextView.text = cliente.usuario_id.toString()
                        nombreClienteTextView.text = cliente.nombre

                        ordersList.addView(cardView)
                    }
                } else {
                    showError("Error al obtener clientes")
                }
            }

            override fun onFailure(call: Call<List<ClienteResponse>>, t: Throwable) {
                showError("Error de red: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MisClientes().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
