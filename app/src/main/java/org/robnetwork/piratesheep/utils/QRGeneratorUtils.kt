package org.robnetwork.piratesheep.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

object QRGeneratorUtils {
    fun generateQRCode(text: String, size: Int, density: Float): Bitmap =
        BarcodeEncoder().createBitmap(
            QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size * density.toInt(),
                size * density.toInt(),
                EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).plus(Pair(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M))
            )
        )
}