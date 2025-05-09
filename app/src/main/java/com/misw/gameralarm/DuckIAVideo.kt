package com.misw.gameralarm

import VideoRequest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.misw.gameralarm.network.ApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.view.SurfaceHolder
import android.view.SurfaceView

class DuckIAVideoFragment : Fragment() {

    private lateinit var surfaceView: SurfaceView
    private var camera: Camera? = null
    private lateinit var btnCargarVideo: Button
    private lateinit var btnProcesar: Button
    private lateinit var textViewStatus: TextView
    private lateinit var emptyImageView: ImageView

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_duck_ia, container, false)

        surfaceView = view.findViewById(R.id.surfaceViewCamera)
        btnCargarVideo = view.findViewById(R.id.btnCargarVideo)
        btnProcesar = view.findViewById(R.id.btnProcesar)
        textViewStatus = view.findViewById(R.id.textViewStatus)

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            initializeCamera()
        }

        btnCargarVideo.setOnClickListener {
            Toast.makeText(context, "Cargar video", Toast.LENGTH_SHORT).show()
            cargarVideo()
        }

        btnProcesar.setOnClickListener {
            Toast.makeText(context, "Procesando...", Toast.LENGTH_SHORT).show()
            procesarVideo()
        }

        return view
    }

    private fun initializeCamera() {
        val holder: SurfaceHolder = surfaceView.holder
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    camera = Camera.open()
                    camera?.setPreviewDisplay(holder)
                    camera?.startPreview()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog("Error al abrir la cámara.")
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Aquí puedes ajustar la configuración de la cámara si es necesario
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                camera?.stopPreview()
                camera?.release() // Liberar la cámara de manera segura
                camera = null
            }
        })
    }

    private fun cargarVideo() {
        // Simulando la carga del video
        val videoRequest = VideoRequest("video_path_here")
        ApiClient.apiService.cargarVideo(videoRequest.toString()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Video cargado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = try {
                        val jsonObj = JSONObject(response.errorBody()?.string() ?: "")
                        jsonObj.getString("error")
                    } catch (e: Exception) {
                        "Error desconocido"
                    }
                    showErrorDialog("Error al cargar video: $errorMessage")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog("Error de red: ${t.message}")
            }
        })
    }

    private fun procesarVideo() {
        // Simulando el procesamiento del video
        ApiClient.apiService.procesarVideo().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Procesamiento exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = try {
                        val jsonObj = JSONObject(response.errorBody()?.string() ?: "")
                        jsonObj.getString("error")
                    } catch (e: Exception) {
                        "Error desconocido"
                    }
                    showErrorDialog("Error al procesar video: $errorMessage")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog("Error de red: ${t.message}")
            }
        })
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Gestor de permisos para la cámara
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera()
            } else {
                showErrorDialog("Permiso de cámara denegado")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Liberar la cámara solo si ha sido inicializada
        camera?.release()
        camera = null
    }

    override fun onResume() {
        super.onResume()
        // Solo reabrir la cámara si es necesario
        if (camera == null) {
            initializeCamera()
        }
    }
}
