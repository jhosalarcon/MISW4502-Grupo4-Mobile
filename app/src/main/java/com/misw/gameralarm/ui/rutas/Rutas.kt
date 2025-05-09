package com.misw.gameralarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class Rutas : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ruta_visitas, container, false)

        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val ordersList: LinearLayout = view.findViewById(R.id.ordersList)

        for (i in 0 until ordersList.childCount) {
            val child = ordersList.getChildAt(i)
            if (child is CardView) {
                child.setOnClickListener {
                    val orderTextView = child.findViewById<TextView>(R.id.orderNumberTextView)
                    val orderText = orderTextView.text.toString()  // e.g. "Order #456765"
                    val orderId = orderText.substringAfter("#").trim()  // e.g. "456765"

                    val bundle = Bundle().apply {
                        putString("orderId", orderId)
                    }

                    findNavController().navigate(R.id.action_misPedidos_to_detallePedido, bundle)
                }
            }
        }
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Rutas().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
