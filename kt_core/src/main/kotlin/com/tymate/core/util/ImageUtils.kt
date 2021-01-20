package com.tymate.core.util


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LightingColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.widget.ImageView
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */

fun ImageView.changeColor(@ColorInt color: Int) {
    val colorFilter = LightingColorFilter(0, color)
    this.colorFilter = colorFilter
}

fun changeColor(drawable: Drawable, @ColorInt color: Int) {
    val colorFilter = LightingColorFilter(0, color)
    val modified = drawable.mutate()
    modified.colorFilter = colorFilter
}

/**
 * Set the alpha component of `color` to be `alpha`.
 */
@CheckResult
@ColorInt
fun modifyAlpha(@ColorInt color: Int,
                @IntRange(from = 0, to = 255) alpha: Int): Int {
    return color and 0x00ffffff or (alpha shl 24)
}

/**
 * Set the alpha component of `color` to be `alpha`.
 */
@CheckResult
@ColorInt
fun modifyAlpha(@ColorInt color: Int,
                @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
    return modifyAlpha(color, (255f * alpha).toInt())
}

fun loadBitmap(filePath: String, requiredWidth: Int, requiredHeight: Int): Bitmap {
    val options = getOptions(filePath, requiredWidth, requiredHeight)
    return BitmapFactory.decodeFile(filePath, options)
}

private fun getOptions(filePath: String, requiredWidth: Int, requiredHeight: Int): BitmapFactory.Options {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)
    options.inSampleSize =
            getScale(options.outWidth, options.outHeight, requiredWidth, requiredHeight)
    options.inJustDecodeBounds = false
    return options
}

private fun getScale(originalWidth: Int, originalHeight: Int, requiredWidth: Int, requiredHeight: Int): Int {
    var scale = 1
    if (originalWidth > requiredWidth || originalHeight > requiredHeight) {
        if (originalWidth < originalHeight)
            scale = Math.round(originalWidth.toFloat() / requiredWidth)
        else
            scale = Math.round(originalHeight.toFloat() / requiredHeight)
    }
    return scale
}

fun Context.getBitmap(@DrawableRes drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else if (drawable is VectorDrawable) {
        getBitmap(drawable)
    } else {
        throw IllegalArgumentException("unsupported drawable type")
    }
}

fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    vectorDrawable.draw(canvas)
    return bitmap
}