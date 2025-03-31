package com.misw.gameralarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReclamarRecompensas : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_reclamar_recompensas, container, false)
        val trofeo1: LinearLayout = view.findViewById(R.id.Trofeo1)
        val trofeo2: LinearLayout = view.findViewById(R.id.Trofeo2)
        val trofeo3: LinearLayout = view.findViewById(R.id.Trofeo3)
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        trofeo1.setOnClickListener {
            showPopup("Trofeo N1", "¡¡Este trofeo ya es tuyo!!")
        }

        trofeo2.setOnClickListener {
            showPopup("Trofeo N2", "Aún te falta llegar a 1.000 puntos para obtener este trofeo. ¡¡Falta poco!!")
        }

        trofeo3.setOnClickListener {
            showPopup("Trofeo N3", "Aún te falta llegar a 1.500 puntos para obtener este trofeo. ¡¡Falta poco!!")
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReclamarRecompensas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog?.window?.setDimAmount(0.7f)
        dialog.show()
    }
}