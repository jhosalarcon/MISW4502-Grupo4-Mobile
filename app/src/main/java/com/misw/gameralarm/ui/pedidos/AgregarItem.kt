package com.misw.gameralarm.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.R
import com.misw.gameralarm.data.model.NuevoProductoRequest
import com.misw.gameralarm.data.model.NuevoProductoResponse
import com.misw.gameralarm.network.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgregarItem : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_nuevo_item, container, false)

        val spinnerNombreItem: Spinner = view.findViewById(R.id.spinnerNombreItem)
        val etDescripcion: EditText = view.findViewById(R.id.etDescripcionItem)
        val etPrecio: EditText = view.findViewById(R.id.etPrecio)
        val etCantidad: EditText = view.findViewById(R.id.etCantidad)
        val etTipo: EditText = view.findViewById(R.id.etTipo)
        val etUbicacion: EditText = view.findViewById(R.id.etUbicacion)
        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        btnGuardar.setOnClickListener {
            val nombre = spinnerNombreItem.selectedItem.toString()
            val descripcion = etDescripcion.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull()
            val cantidad = etCantidad.text.toString().toIntOrNull()
            val tipo = etTipo.text.toString()
            val ubicacion = etUbicacion.text.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precio != null && cantidad != null && tipo.isNotEmpty() && ubicacion.isNotEmpty()) {
                val request = NuevoProductoRequest(nombre, descripcion, precio, cantidad, tipo, ubicacion)

                ApiClient.apiService.agregarProducto(request).enqueue(object : Callback<NuevoProductoResponse> {
                    override fun onResponse(call: Call<NuevoProductoResponse>, response: Response<NuevoProductoResponse>) {
                        if (response.isSuccessful && response.code() == 201) {
                            showSuccessDialog("Producto creado exitosamente")
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val json = JSONObject(errorBody ?: "")
                                json.getString("error")
                            } catch (e: Exception) {
                                "No se pudo guardar el producto"
                            }
                            showErrorDialog(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<NuevoProductoResponse>, t: Throwable) {
                        showErrorDialog("Error de red: ${t.message}")
                    }
                })
            } else {
                showErrorDialog("Debe ingresar todos los campos correctamente")
            }
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Éxito")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_dashboard_to_mis_pedidos)
        }
        builder.create().show()
    }
}
