package com.misw.gameralarm.ui.items

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.R
import com.misw.gameralarm.data.model.NuevoProductoResponse
import com.misw.gameralarm.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgregarItem : Fragment() {

    private lateinit var spinnerNombreItem: Spinner
    private lateinit var tvDescripcion: TextView
    private lateinit var tvPrecio: TextView
    private lateinit var etCantidad: EditText
    private lateinit var tvTipo: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageButton

    private var productosList: List<NuevoProductoResponse> = emptyList()
    private var productoSeleccionado: NuevoProductoResponse? = null
    private var authToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_nuevo_item, container, false)

        spinnerNombreItem = view.findViewById(R.id.spinnerNombreItem)
        tvDescripcion = view.findViewById(R.id.etDescripcionItem)
        tvPrecio = view.findViewById(R.id.etPrecio)
        etCantidad = view.findViewById(R.id.etCantidad)
        tvTipo = view.findViewById(R.id.etTipo)
        tvUbicacion = view.findViewById(R.id.etUbicacion)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnBack = view.findViewById(R.id.btnBack)

        authToken = getAuthToken()

        cargarProductos()

        spinnerNombreItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (productosList.isNotEmpty()) {
                    productoSeleccionado = productosList[position]
                    llenarCamposConProducto(productoSeleccionado!!)
                    // Aquí ya no guardamos nada, solo seleccionamos el producto
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        btnGuardar.setOnClickListener {
            // No hacer POST, solo guardar el producto_id seleccionado
            productoSeleccionado?.producto_id?.let {
                guardarProductIdEnPrefs(it)
                showSuccessDialog("Producto seleccionado guardado correctamente")
            } ?: run {
                showErrorDialog("Debe seleccionar un producto")
            }
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    private fun getAuthToken(): String? {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("auth_token", null)
    }

    private fun cargarProductos() {
        if (authToken == null) {
            showErrorDialog("Token no encontrado. Vuelva a iniciar sesión.")
            return
        }

        ApiClient.apiService.obtenerProductos("Bearer $authToken")
            .enqueue(object : Callback<List<NuevoProductoResponse>> {
                override fun onResponse(call: Call<List<NuevoProductoResponse>>, response: Response<List<NuevoProductoResponse>>) {
                    if (response.isSuccessful) {
                        productosList = response.body() ?: emptyList()

                        val nombresProductos = productosList.map { it.nombre }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresProductos)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerNombreItem.adapter = adapter
                    } else {
                        showErrorDialog("Error al cargar productos")
                    }
                }

                override fun onFailure(call: Call<List<NuevoProductoResponse>>, t: Throwable) {
                    showErrorDialog("Error de red al cargar productos: ${t.message}")
                }
            })
    }

    private fun llenarCamposConProducto(producto: NuevoProductoResponse) {
        tvDescripcion.text = producto.descripcion
        tvPrecio.text = producto.precio_unitario.toString()
        etCantidad.setText(producto.cantidad.toString())
        tvTipo.text = producto.tipo
        tvUbicacion.text = producto.ubicacion
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Éxito")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.action_dashboard_to_nuevo_pedido)
            }
            .create()
            .show()
    }

    private fun guardarProductIdEnPrefs(productId: Int) {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val existingIds = sharedPref.getString("product_ids", "")
        val existingPrecios = sharedPref.getString("product_prices", "")

        val updatedIds = if (existingIds.isNullOrEmpty()) {
            productId.toString()
        } else {
            "$existingIds,$productId"
        }

        val precioUnitario = productoSeleccionado?.precio_unitario ?: 0.0
        val updatedPrecios = if (existingPrecios.isNullOrEmpty()) {
            precioUnitario.toString()
        } else {
            "$existingPrecios,$precioUnitario"
        }

        with(sharedPref.edit()) {
            putString("product_ids", updatedIds)
            putString("product_prices", updatedPrecios)
            apply()
        }
    }

}
