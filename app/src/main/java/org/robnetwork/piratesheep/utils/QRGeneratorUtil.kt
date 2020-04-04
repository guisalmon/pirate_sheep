package org.robnetwork.piratesheep.utils

import android.graphics.Bitmap
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver

object QRGeneratorUtil {

    fun generateQRCode(text: String, onQRCodeGenerated: (Bitmap) -> Unit) = onQRCodeGenerated(QRGEncoder(text, QRGContents.Type.TEXT).bitmap)

    fun saveQRCode(path: String, name: String, bitmap: Bitmap, onQRCodeSaved: (Boolean) -> Unit) = onQRCodeSaved(QRGSaver().save(path, name.trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG))
}