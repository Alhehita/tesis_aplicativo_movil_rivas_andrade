package com.uce.tesisrivasandrade.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    /**
     * Comprime y convierte un Bitmap a Base64.
     */
    fun encodeImageToBase64(bitmap: Bitmap): String {
        // Primero redimensionamos la imagen si es muy grande
        val resizedBitmap = resizeBitmap(bitmap, 800)
        
        val outputStream = ByteArrayOutputStream()
        // Bajamos la calidad al 50% para reducir peso considerablemente
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Redimensiona un bitmap manteniendo su relación de aspecto.
     */
    private fun resizeBitmap(bitmap: Bitmap, maxWidthHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidthHeight && height <= maxWidthHeight) return bitmap

        val aspectRatio: Float = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxWidthHeight
            newHeight = (maxWidthHeight / aspectRatio).toInt()
        } else {
            newHeight = maxWidthHeight
            newWidth = (maxWidthHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun decodeBase64ToImage(base64String: String): Bitmap {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
