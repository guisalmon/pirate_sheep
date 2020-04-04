package org.robnetwork.piratesheep.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import com.google.zxing.qrcode.encoder.QRCode

object ImageUtils {
    fun drawableToBitmap(context: Context, @DrawableRes res: Int): Bitmap =
        BitmapFactory.decodeResource(context.resources, res).copy(Bitmap.Config.ARGB_8888, true)

    fun writeTextToBitmapAt(text: String, bitmap: Bitmap, x: Int, y: Int, size: Int, scale: Float, isLeftAligned: Boolean = true): Bitmap {
        Canvas(bitmap).let { canvas ->
            Paint().let { paint ->
                paint.color = Color.BLACK
                paint.textSize = size*scale
                paint.textAlign = if (isLeftAligned) Paint.Align.LEFT else Paint.Align.RIGHT
                canvas.drawText(text, x*scale, y*scale, paint)
            }
        }
        return bitmap
    }

    fun writeQrCodeToCanvas(qrCode: Bitmap, bitmap: Bitmap, x: Int, y: Int, scale: Float): Bitmap {
        Canvas(bitmap).let { canvas ->
            canvas.drawColor(Color.WHITE)
            Paint().let { paint ->
                paint.color = Color.BLACK
                canvas.drawBitmap(qrCode, x*scale, y*scale, paint)
            }
        }
        return bitmap
    }
}