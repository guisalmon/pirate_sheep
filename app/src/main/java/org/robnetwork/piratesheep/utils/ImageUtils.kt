package org.robnetwork.piratesheep.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes

object ImageUtils {
    fun drawableToBitmap(context: Context, @DrawableRes res: Int, onDrawableDecoded: (Bitmap?) -> Unit) {
        onDrawableDecoded(BitmapFactory.decodeResource(context.resources, res).copy(Bitmap.Config.ARGB_8888, true))
    }

    fun writeTextToBitmapAt(text: String, bitmap: Bitmap, x: Int, y: Int, size: Int, scale: Float): Bitmap {
        Canvas(bitmap).let { canvas ->
            Paint().let { paint ->
                paint.color = Color.BLACK
                paint.textSize = size*scale
                val xD = x*scale
                val yD = y*scale
                canvas.drawText(text, xD, yD, paint)
            }
        }
        return bitmap
    }
}