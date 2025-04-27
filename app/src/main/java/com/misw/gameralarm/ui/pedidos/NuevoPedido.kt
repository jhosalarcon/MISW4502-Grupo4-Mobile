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

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var layoutCards: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nuevo_pedido, container, false)

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

            ApiClient.apiService.obtenerProducto(id).enqueue(object : Callback<NuevoProductoResponse> {
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




    private fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Dashboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
