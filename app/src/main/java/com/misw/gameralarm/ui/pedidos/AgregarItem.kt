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
import com.misw.gameralarm.data.model.NuevoProductoRequest
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

    private var productosList: List<NuevoProductoRequest> = emptyList()
    private var productoSeleccionado: NuevoProductoRequest? = null
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
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnGuardar.setOnClickListener {
            guardarProducto()
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
            .enqueue(object : Callback<List<NuevoProductoRequest>> {
                override fun onResponse(call: Call<List<NuevoProductoRequest>>, response: Response<List<NuevoProductoRequest>>) {
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

                override fun onFailure(call: Call<List<NuevoProductoRequest>>, t: Throwable) {
                    showErrorDialog("Error de red al cargar productos: ${t.message}")
                }
            })
    }

    private fun llenarCamposConProducto(producto: NuevoProductoRequest) {
        tvDescripcion.text = producto.descripcion
        tvPrecio.text = producto.precio_unitario.toString()
        etCantidad.setText(producto.cantidad.toString())
        tvTipo.text = producto.tipo
        tvUbicacion.text = producto.ubicacion
    }

    private fun guardarProducto() {
        val nombre = spinnerNombreItem.selectedItem.toString()
        val descripcion = tvDescripcion.text.toString()
        val precio = tvPrecio.text.toString().toDoubleOrNull()
        val cantidad = etCantidad.text.toString().toIntOrNull()
        val tipo = tvTipo.text.toString()
        val ubicacion = tvUbicacion.text.toString()

        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precio != null && cantidad != null && tipo.isNotEmpty() && ubicacion.isNotEmpty()) {
            val request = NuevoProductoRequest(nombre, descripcion, precio, cantidad, tipo, ubicacion)

            productoSeleccionado?.let {
                ApiClient.apiService.agregarProducto("Bearer $authToken", request)
                    .enqueue(object : Callback<NuevoProductoResponse> {
                        override fun onResponse(call: Call<NuevoProductoResponse>, response: Response<NuevoProductoResponse>) {
                            if (response.isSuccessful) {
                                val nuevoProducto = response.body()
                                nuevoProducto?.let {
                                    // GUARDAMOS el product_id en SharedPreferences
                                    guardarProductIdEnPrefs(it.producto_id)
                                    showSuccessDialog("Producto agregado al pedido exitosamente")
                                } ?: run {
                                    showErrorDialog("Respuesta vacía del servidor")
                                }
                            } else {
                                showErrorDialog("Error al agregar el producto")
                            }
                        }

                        override fun onFailure(call: Call<NuevoProductoResponse>, t: Throwable) {
                            showErrorDialog("Error de red al agregar: ${t.message}")
                        }
                    })
            } ?: run {
                showErrorDialog("Seleccione un producto")
            }
        } else {
            showErrorDialog("Debe ingresar todos los campos correctamente")
        }
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

        val updatedIds = if (existingIds.isNullOrEmpty()) {
            productId.toString()
        } else {
            "$existingIds,$productId"
        }

        with(sharedPref.edit()) {
            putString("product_ids", updatedIds)
            apply()
        }
    }


}
