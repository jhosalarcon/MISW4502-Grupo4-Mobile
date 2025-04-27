package com.misw.gameralarm

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.data.model.NuevoProductoRequest
import com.misw.gameralarm.data.model.NuevoProductoResponse
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NuevoPedido : Fragment() {

    private lateinit var layoutCards: LinearLayout
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nuevo_pedido, container, false)

        // Obtener el token de autorización
        token = getAuthToken()

        layoutCards = view.findViewById(R.id.layoutCards)

        val btnNuevoItem: Button = view.findViewById(R.id.btnNuevoItem)
        btnNuevoItem.setOnClickListener {
            findNavController().navigate(R.id.action_nuevo_pedido_to_agregar_nuevo_item)
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        mostrarProductosAgregados(view)

        return view
    }

    // Obtener el token de autorización desde SharedPreferences
    private fun getAuthToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        token = sharedPref.getString("auth_token", null)

        // También puedes considerar sobrescribirlo si viene como argumento
        token = arguments?.getString("token") ?: token
        return token
    }

    private fun mostrarProductosAgregados(view: View) {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val idsString = sharedPref.getString("product_ids", null)

        val productIds: List<Int> = idsString
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        val emptyImage: ImageView = view.findViewById(R.id.emptyImage)
        val textAgregarItem: TextView = view.findViewById(R.id.textAgregarItem)
        val layoutCards: LinearLayout = view.findViewById(R.id.layoutCards)

        if (productIds.isEmpty()) {
            emptyImage.visibility = View.VISIBLE
            textAgregarItem.visibility = View.VISIBLE
            layoutCards.removeAllViews()
            return
        }

        emptyImage.visibility = View.GONE
        textAgregarItem.visibility = View.GONE
        layoutCards.removeAllViews()

        for (id in productIds) {
            val cardView = CardView(requireContext()).apply {
                radius = 16f
                cardElevation = 8f
                useCompatPadding = true
                setContentPadding(32, 32, 32, 32)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 32)
                }
            }

            val textView = TextView(requireContext()).apply {
                text = "Cargando producto $id..."
                textSize = 18f
            }

            cardView.addView(textView)
            layoutCards.addView(cardView)

            // Verificar si el token de autorización es nulo
            if (token == null) {
                textView.text = "Token de autorización no encontrado"
                return
            }

            // Hacer la llamada a la API para obtener los datos del producto con el header Authorization
            ApiClient.apiService.obtenerProducto("Bearer $token", id).enqueue(object : Callback<NuevoProductoResponse> {
                override fun onResponse(call: Call<NuevoProductoResponse>, response: Response<NuevoProductoResponse>) {
                    if (response.isSuccessful) {
                        val producto = response.body()
                        textView.text = producto?.descripcion ?: "Sin descripción"
                    } else {
                        textView.text = "No encontrado ($id)"
                    }
                }

                override fun onFailure(call: Call<NuevoProductoResponse>, t: Throwable) {
                    textView.text = "Error cargando ($id)"
                }
            })
        }
    }
}


