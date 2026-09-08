package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter

fun generateQRCode(text: String, size: Int = 512): Bitmap? {
    if (text.isBlank()) return null
    try {
        val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (e: Exception) {
        return null
    }
}

fun generateBarcode(text: String, width: Int = 800, height: Int = 200): Bitmap? {
    if (text.isBlank()) return null
    try {
        val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, width, height)
        val bitWidth = bitMatrix.width
        val bitHeight = bitMatrix.height
        val bitmap = Bitmap.createBitmap(bitWidth, bitHeight, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until bitWidth) {
            for (y in 0 until bitHeight) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (e: Exception) {
        return null
    }
}
