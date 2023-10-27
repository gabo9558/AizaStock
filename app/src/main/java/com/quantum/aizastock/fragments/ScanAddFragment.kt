package com.quantum.aizastock.fragments

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.common.util.concurrent.ListenableFuture
import com.quantum.aizastock.R
import com.quantum.aizastock.common.QrCodeAnalyzer
import com.quantum.aizastock.databinding.ScanFragmentBinding
import java.util.concurrent.Executors

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class ScanAddFragment: Fragment() {

    private var _binding: ScanFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScanFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cameraExecutor.shutdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        if (allPermissionsGranted()){
            cameraProvider()
        }

    }

    private fun cameraProvider(){
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider){
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.previewView.width, binding.previewView.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrResult ->
                    println("Barcode Scanned: ${qrResult.text}")

                    binding.previewView.post {

                        AlertDialog.Builder(requireContext())
                            .setMessage("Agregar el IMEI: ${qrResult.text}")
                            .setPositiveButton("Agregar") { _, _ ->

                                //ACTION
                                val bundle = Bundle()
                                bundle.apply {
                                    putString("IMEI", qrResult.text)
                                }
                                view?.let { viewx ->
                                    Navigation.findNavController(viewx).navigate(R.id.scan_add_to_description_add, bundle)
                                }
                            }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                dialog.dismiss()
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(this, cameraSelector, preview, it)
                            }
                            .create().show()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
                    }
                })
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        } catch (e: Exception){
            println("error: $e")
        }
    }

    @Deprecated("permission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionsGranted()){
                cameraProvider()
            }else{
                Toast.makeText(requireContext(), "No has aceptado los permisos de camara.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}