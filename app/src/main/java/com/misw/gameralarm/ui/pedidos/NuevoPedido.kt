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
class NuevoPedido : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_nuevo_pedido, container, false)

        val btnNuevoItem: Button = view.findViewById(R.id.btnNuevoItem)
        val btnCrearPedido: Button = view.findViewById(R.id.btnCrearPedido)

        btnNuevoItem.setOnClickListener {
            findNavController().navigate(R.id.action_nuevo_pedido_to_agregar_nuevo_item)
        }

        btnCrearPedido.setOnClickListener {
            showPopup("Historial alarmas", "Esta funcionalidad no está en el alcance")
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