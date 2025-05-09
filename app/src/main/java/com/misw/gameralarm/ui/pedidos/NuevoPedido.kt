package com.misw.gameralarm

import DetallePedidoRequest
import OrderRequest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.data.model.NuevoProductoResponse
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NuevoPedido : Fragment() {

    private lateinit var layoutCards: LinearLayout
    private lateinit var btnGuardarPedido: Button
    private lateinit var btnCancelarPedido: Button
    private var token: String? = null
    private lateinit var labelTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nuevo_pedido, container, false)

        token = getAuthToken()
        layoutCards = view.findViewById(R.id.layoutCards)
        btnGuardarPedido = view.findViewById(R.id.btnGuardarPedido)
        btnCancelarPedido = view.findViewById(R.id.btnCancelarPedido)
        labelTotal = view.findViewById(R.id.labelTotal)

        if (hayProductosGuardados()) {
            btnGuardarPedido.visibility = View.VISIBLE
            btnCancelarPedido.visibility = View.VISIBLE
        }

        val btnNuevoItem: Button = view.findViewById(R.id.btnNuevoItem)
        btnNuevoItem.setOnClickListener {
            findNavController().navigate(R.id.action_nuevo_pedido_to_agregar_nuevo_item)
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        btnGuardarPedido.setOnClickListener {
            guardarPedido()
        }

        btnCancelarPedido.setOnClickListener {
            cancelarPedido()
        }

        mostrarProductosAgregados(view)
        calcularYMostrarTotal()

        return view
    }

    private fun getAuthToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        token = sharedPref.getString("auth_token", null)
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

            if (token == null) {
                textView.text = "Token de autorización no encontrado"
                return
            }

            ApiClient.apiService.obtenerProducto("Bearer $token", id).enqueue(object : Callback<NuevoProductoResponse> {
                override fun onResponse(call: Call<NuevoProductoResponse>, response: Response<NuevoProductoResponse>) {
                    if (response.isSuccessful) {
                        val producto = response.body()
                        textView.text = producto?.nombre ?: "Sin nombre"
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
    private fun hayProductosGuardados(): Boolean {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val ids = sharedPref.getString("product_ids", null)
        return !ids.isNullOrEmpty()
    }

    private fun guardarPedido() {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        val userId = sharedPref.getString("user_id", null)?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(requireContext(), "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val idsString = sharedPref.getString("product_ids", null)
        val quantitiesString = sharedPref.getString("product_quantities", null)
        val pricesString = sharedPref.getString("product_prices", null)

        val ids = idsString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: return
        val quantities = quantitiesString?.split(",")?.mapNotNull { it.toIntOrNull() } ?: return
        val prices = pricesString?.split(",")?.mapNotNull { it.toDoubleOrNull() } ?: return

        if (ids.size != quantities.size || ids.size != prices.size) {
            Toast.makeText(requireContext(), "Error: datos de productos incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        val detalles = ids.indices.map {
            DetallePedidoRequest(
                id_producto = ids[it],
                cantidad = quantities[it],
                precio_unitario = prices[it]
            )
        }

        val pedido = OrderRequest(
            id_cliente = userId,
            id_vendedor = 2,
            detalles = detalles
        )

        val call = ApiClient.apiService.guardarPedido("Bearer $token", pedido)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Pedido enviado exitosamente", Toast.LENGTH_SHORT).show()
                    limpiarProductosGuardados()
                    findNavController().navigate(R.id.action_dashboard_to_mis_pedidos)
                } else {
                    Toast.makeText(requireContext(), "Error al guardar pedido", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun limpiarProductosGuardados() {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("product_ids")
            remove("product_prices")
            remove("product_quantities")
            apply()
        }
    }

    private fun cancelarPedido() {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("product_ids")
            remove("product_prices")
            remove("product_quantities")
            apply()
        }

        Toast.makeText(requireContext(), "Pedido cancelado", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_home_to_dashboard)
    }

    private fun calcularYMostrarTotal() {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val preciosString = sharedPref.getString("product_prices", null)
        val cantidadesString = sharedPref.getString("product_quantities", null)

        val precios: List<Double> = preciosString
            ?.split(",")
            ?.mapNotNull { it.toDoubleOrNull() }
            ?: emptyList()

        val cantidades: List<Int> = cantidadesString
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        if (precios.size != cantidades.size) {
            labelTotal.text = "Total: Error en datos"
            return
        }

        val total = precios.indices.sumOf { precios[it] * cantidades[it] }

        labelTotal.text = "Total: $%.2f".format(total)
    }


}


