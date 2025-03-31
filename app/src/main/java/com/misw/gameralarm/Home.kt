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

class Home : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val etEmail: EditText = view.findViewById(R.id.etEmail)
        val etPassword: EditText = view.findViewById(R.id.etPassword)
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val btnCreateAccount: Button = view.findViewById(R.id.btnCreateAccount)
        val tvForgotPassword: TextView = view.findViewById(R.id.tvForgotPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                findNavController().navigate(R.id.action_home_to_dashboard)
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

    private fun showPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Crear cuenta")
        builder.setMessage("Esta funcionalidad no está en el alcance")
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
