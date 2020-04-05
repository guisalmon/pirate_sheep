package org.robnetwork.piratesheep.utils

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.annotation.DrawableRes

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

    fun setBackgroundWhite(bitmap: Bitmap): Bitmap {
        Canvas(bitmap).drawColor(Color.WHITE)
        return bitmap
    }

    fun writeQrCodeToCanvas(qrCode: Bitmap, bitmap: Bitmap, x: Int, y: Int, scale: Float): Bitmap {
        Canvas(bitmap).let { canvas ->
            Paint().let { paint ->
                paint.color = Color.BLACK
                canvas.drawBitmap(qrCode, x*scale, y*scale, paint)
            }
        }
        return bitmap
    }

    fun resizeBitmapToScreen(context: Context, bitmap: Bitmap): Bitmap {
        val width = context.resources.displayMetrics.widthPixels
        val height = ((bitmap.height.toFloat()/bitmap.width.toFloat())*width.toFloat()).toInt()
        Log.e(ImageUtils::class.java.simpleName, "bitmap : ${bitmap.width}x${bitmap.height}, pdf : ${width}x$height")
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}