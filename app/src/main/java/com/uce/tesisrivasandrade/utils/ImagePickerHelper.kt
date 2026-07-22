package com.uce.tesisrivasandrade.utils

import android.graphics.Bitmap
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File

/**
 * Helper que centraliza la lógica de captura de imágenes (cámara + galería)
 * para evitar duplicación en DetalleNovedadFragment y ReportarNovedadFragment.
 */
class ImagePickerHelper(
    private val fragment: Fragment,
    private val onImageReady: (Bitmap) -> Unit
) {
    private var latestTmpUri: Uri? = null

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) prepararYTomarFoto()
            else Toast.makeText(fragment.requireContext(), "Se requiere permiso de cámara", Toast.LENGTH_LONG).show()
        }

    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) latestTmpUri?.let { uri -> procesarImagen(uri) }
        }

    private val selectImageLauncher: ActivityResultLauncher<String> =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { procesarImagen(it) }
        }

    fun mostrarOpciones(titulo: String = "Seleccionar Imagen") {
        val opciones = arrayOf("Tomar Foto", "Seleccionar de Galería")
        AlertDialog.Builder(fragment.requireContext())
            .setTitle(titulo)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndTakePhoto()
                    1 -> selectImageLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndTakePhoto() {
        val ctx = fragment.requireContext()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            prepararYTomarFoto()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun prepararYTomarFoto() {
        try {
            val ctx = fragment.requireContext()
            val storageDir = ctx.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val tmpFile = File.createTempFile("img_${System.currentTimeMillis()}", ".jpg", storageDir).apply {
                createNewFile()
                deleteOnExit()
            }
            latestTmpUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", tmpFile)
            takePictureLauncher.launch(latestTmpUri!!)
        } catch (e: Exception) {
            Toast.makeText(fragment.requireContext(), "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarImagen(uri: Uri) {
        try {
            val ctx = fragment.requireContext()
            // Actualizado a 4 MB para coincidir con el backend
            val tamanoMaximo = 4 * 1024 * 1024 
            val tamanoArchivo = obtenerTamanoArchivo(ctx, uri)

            if (tamanoArchivo > tamanoMaximo) {
                AlertDialog.Builder(ctx)
                    .setTitle("Imagen muy pesada")
                    .setMessage("La imagen seleccionada pesa ${tamanoArchivo / 1024 / 1024} MB. El límite permitido es 4 MB.")
                    .setPositiveButton("Entendido", null)
                    .show()
                return
            }

            val inputStream = ctx.contentResolver.openInputStream(uri)
            val options = android.graphics.BitmapFactory.Options().apply { inSampleSize = 2 }
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream, null, options)

            if (bitmap != null) {
                onImageReady(bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(fragment.requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerTamanoArchivo(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
        } catch (e: Exception) { 0L }
    }
}
