package com.misw.gameralarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.gameralarm.data.model.LoginRequest
import com.misw.gameralarm.data.model.LoginResponse
import com.misw.gameralarm.network.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etEmail: EditText = view.findViewById(R.id.etEmail)
        val etPassword: EditText = view.findViewById(R.id.etPassword)
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val btnCreateAccount: Button = view.findViewById(R.id.btnCreateAccount)
        val tvForgotPassword: TextView = view.findViewById(R.id.tvForgotPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val request = LoginRequest(email, password)

                ApiClient.apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            loginResponse?.token?.let { token ->
                                saveAuthToken(token)
                            }
                            loginResponse?.id?.let { id ->
                                saveUserId(id.toString())
                            }

                            when (loginResponse?.rol) {
                                "CLIENTE" -> {
                                    findNavController().navigate(R.id.action_home_to_dashboard)
                                }
                                "VENDEDOR" -> {
                                    findNavController().navigate(R.id.action_home_to_dashboard_vendedor)
                                }
                                else -> {
                                    showErrorDialog("Rol no reconocido: ${loginResponse?.rol}")
                                }
                            }
                        } else {
                            val errorMessage = try {
                                val jsonObj = JSONObject(response.errorBody()?.string() ?: "")
                                jsonObj.getString("error")
                            } catch (e: Exception) {
                                "Error desconocido"
                            }
                            showErrorDialog("Inicio de sesión fallido: $errorMessage")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showErrorDialog("Error de red: ${t.message}")
                    }
                })
            } else {
                showErrorDialog("Debe ingresar usuario y contraseña")
            }
        }

        btnCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_crear_cuenta)
        }

        tvForgotPassword.setOnClickListener {
            showErrorDialog("Funcionalidad no implementada")
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

    private fun saveAuthToken(token: String) {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        sharedPref.edit().putString("auth_token", token).apply()
    }

    private fun saveUserId(id: String) {
        val sharedPref = requireContext().getSharedPreferences("auth_prefs", 0)
        sharedPref.edit().putString("user_id", id).apply()
    }
}