package com.misw.gameralarm

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Recompensas : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_recompensas, container, false)

        val btnCatalogo: Button = view.findViewById(R.id.btnCatalogo)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val tvObtenerPuntos: TextView = view.findViewById(R.id.tvObtenerPuntos)

        val content = SpannableString("¿Cómo obtener puntos?")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tvObtenerPuntos.setText(content)

        btnCatalogo.setOnClickListener {
            findNavController().navigate(R.id.action_recompensas_to_reclamar)
        }

        progressBar.setOnClickListener {
            findNavController().navigate(R.id.action_recompensas_to_reclamar)
        }

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        tvObtenerPuntos.setOnClickListener {
            showPopup()
        }

        return view
    }
    private fun showPopup() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¿Cómo ganar puntos?")

        val message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco.\nLink "
        val spannableMessage = SpannableString(message)

        spannableMessage.setSpan(UnderlineSpan(), 184, 188, 0)
        builder.setMessage(spannableMessage)

        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Recompensas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}