package com.misw.gameralarm.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.R
import com.misw.gameralarm.data.model.CrearCuentaRequest
import com.misw.gameralarm.data.model.CrearCuentaResponse
import com.misw.gameralarm.network.ApiClient
import org.json.JSONObject

class CrearCuenta : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_cuenta, container, false)

        val etNombre: EditText = view.findViewById(R.id.etNombre)
        val etTelefono: EditText = view.findViewById(R.id.etTelefono)
        val etEmail: EditText = view.findViewById(R.id.etEmail)
        val etPassword: EditText = view.findViewById(R.id.etPassword)
        val btnCreateAccount: Button = view.findViewById(R.id.btnCreateAccount)

        btnCreateAccount.setOnClickListener {
            val nombre = etNombre.text.toString()
            val telefono = etTelefono.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty() && telefono.isNotEmpty()) {
                val request = CrearCuentaRequest(nombre, telefono, email, password)

                ApiClient.apiService.registerUser(request).enqueue(object : retrofit2.Callback<CrearCuentaResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<CrearCuentaResponse>,
                        response: retrofit2.Response<CrearCuentaResponse>
                    ) {
                        if (response.isSuccessful && response.code() == 201) {
                            findNavController().navigate(R.id.action_home_to_dashboard)
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val json = JSONObject(errorBody ?: "")
                                json.getString("error")
                            } catch (e: Exception) {
                                "Registro fallido"
                            }
                            showErrorDialog(errorMessage)
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<CrearCuentaResponse>, t: Throwable) {
                        showErrorDialog("Error de red: ${t.message}")
                    }
                })
            } else {
                showErrorDialog("Debe ingresar todos los campos")
            }
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

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
}