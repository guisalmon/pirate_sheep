package org.robnetwork.piratesheep.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object ImageUtils {
    fun drawableToBitmap(drawable: Drawable, onDrawableDecoded: (Bitmap?) -> Unit) =
        onDrawableDecoded((drawable as? BitmapDrawable)?.bitmap)


}