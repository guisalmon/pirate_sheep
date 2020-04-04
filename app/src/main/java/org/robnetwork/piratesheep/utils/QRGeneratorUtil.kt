package org.robnetwork.piratesheep.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

object QRGeneratorUtil {
    fun generateQRCode(text: String, size: Int, density: Float, onQRCodeGenerated: (Bitmap) -> Unit) = onQRCodeGenerated(
        BarcodeEncoder().createBitmap(MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size*density.toInt(), size*density.toInt()))
    )
}