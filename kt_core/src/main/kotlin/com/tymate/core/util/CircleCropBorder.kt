package com.tymate.core.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class CircleCropBorder(var borderWidth: Int, var borderColor: Int) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val circle = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight)
        return addBorderToCircularBitmap(circle, borderWidth, borderColor)
    }

    // Bitmap doesn't implement equals, so == and .equals are equivalent here.
    override fun equals(other: Any?): Boolean {
        return other is CircleCropBorder
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    // Custom method to add a border around circular bitmap
    protected fun addBorderToCircularBitmap(srcBitmap: Bitmap, borderWidth: Int, borderColor: Int): Bitmap {
        // Calculate the circular bitmap width with border
        val dstBitmapWidth = srcBitmap.width + borderWidth * 2

        val dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(dstBitmap)
        val floatBorderWidth = borderWidth.toFloat()
        canvas.drawBitmap(srcBitmap, floatBorderWidth, floatBorderWidth, null)

        val paint = Paint()
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = floatBorderWidth
        paint.isAntiAlias = true

        /*
            public void drawCircle (float cx, float cy, float radius, Paint paint)
                Draw the specified circle using the specified paint. If radius is <= 0, then nothing
                will be drawn. The circle will be filled or framed based on the Style in the paint.

            Parameters
                cx : The x-coordinate of the center of the cirle to be drawn
                cy : The y-coordinate of the center of the cirle to be drawn
                radius : The radius of the cirle to be drawn
                paint : The paint used to draw the circle
        */
        // Draw the circular border around circular bitmap
        canvas.drawCircle(
                canvas.width / 2f, // cx
                canvas.width / 2f, // cy
                canvas.width / 2f - borderWidth / 2f, // Radius
                paint // Paint
        )

        // Free the native object associated with this bitmap.
        srcBitmap.recycle()

        // Return the bordered circular bitmap
        return dstBitmap
    }

    companion object {
        // The version of this transformation, incremented to correct an error in a previous version.
        // See #455.
        private val VERSION = 1
        private val ID = "com.bumptech.glide.load.resource.bitmap.CircleCropBorder.$VERSION"
        private val ID_BYTES = ID.toByteArray(CHARSET)
    }
}