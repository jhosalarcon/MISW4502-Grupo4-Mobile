package com.misw.gameralarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Dashboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class Dashboard : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val btnPerfil: Button = view.findViewById(R.id.btnPerfil)
        val btnHistorial: Button = view.findViewById(R.id.btnHistorial)
        val btnAlarmasActivas: Button = view.findViewById(R.id.btnAlarmasActivas)
        val btnRecompensas: Button = view.findViewById(R.id.btnRecompensas)
        val btnCrearAlarma: Button = view.findViewById(R.id.btnCrearAlarma)

        btnRecompensas.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_recompensas)
        }

        btnHistorial.setOnClickListener {
            showPopup("Historial alarmas", "Esta funcionalidad no está en el alcance")
        }

        btnCrearAlarma.setOnClickListener {
            showPopup("Crear alarma", "Esta funcionalidad no está en el alcance")
        }

        btnAlarmasActivas.setOnClickListener {
            showPopup("Alarmas activas", "Esta funcionalidad no está en el alcance")
        }

        btnPerfil.setOnClickListener {
            showPopup("Mi perfil", "Esta funcionalidad no está en el alcance")
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
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