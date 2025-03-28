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
                findNavController().navigate(R.id.action_home_to_dashboard)
            } else {
                showErrorDialog("Debe ingresar todos los campos")
            }
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
