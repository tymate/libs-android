package com.tymate.core.databinding

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.tymate.core.util.changeColor
import com.tymate.core.widget.GlideApp
import java.io.File


@BindingAdapter(value = ["image", "default", "defaultVector"], requireAll = false)
fun ImageView.loadImage(
    url: String?,
    @DrawableRes imageRes: Int?,
    @DrawableRes defaultVector: Int?
) {
    if (TextUtils.isEmpty(url)) {
        if (imageRes != 0) {
            loadImageRes(imageRes)
        } else {
            loadImageResource(defaultVector, 0, 0)
        }
    } else {
        var request = GlideApp.with(context)
            .load(url)
        request = request.apply(RequestOptions.skipMemoryCacheOf(true))
        request = request.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
        request
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter(value = ["image", "placeHolder"], requireAll = false)
fun ImageView.loadImage(
    uri: Uri?,
    @DrawableRes placeHolder: Int?
) {
    when {
        uri != null -> GlideApp.with(context).load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
        placeHolder != 0 -> loadImageResource(placeHolder, 0, 0)
        else -> setImageDrawable(null)
    }
}

@BindingAdapter(value = ["image", "placeHolder"], requireAll = false)
fun ImageView.loadImage(
    file: File?,
    @DrawableRes placeHolder: Int?
) {
    when {
        file != null -> GlideApp.with(context).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
        placeHolder != 0 -> loadImageResource(placeHolder, 0, 0)
        else -> setImageDrawable(null)
    }
}

@BindingAdapter(value = ["vector"])
fun ImageView.loadImage(@DrawableRes vector: Int?) {
    if (vector != 0) {
        loadImageResource(vector, 0, 0)
    } else {
        setImageDrawable(null)
    }
}

@BindingAdapter(value = ["image"])
fun ImageView.loadImageRes(@DrawableRes image: Int?) {
    if (image != null && image != 0) {
        setImageResource(image)
    }
}

@BindingAdapter(value = ["imageResource", "placeHolder", "imageColor"], requireAll = false)
fun ImageView.loadImageResource(
    imageRes: Int?,
    placeHolderRes: Int?,
    color: Int?
) {
    if (imageRes != 0 && imageRes != null) {
        setImageResource(imageRes)
    } else if (placeHolderRes != 0 && imageRes != null) {
        setImageResource(imageRes)
    } else {
        setImageDrawable(null)
    }
    if (color == 0) {
        clearColorFilter()
    } else if (color != null) {
        changeColor(color)
    }
}

@BindingAdapter(value = ["imageCircle", "placeHolder"], requireAll = false)
fun ImageView.loadImage(
    url: String?,
    placeHolder: Int
) {
    if (!TextUtils.isEmpty(url)) {
        val requestManager = GlideApp.with(context)
        var request: RequestBuilder<*>
        if (url!!.startsWith("/")) {
            request = requestManager.load(File(url))
            request = request.apply(RequestOptions.skipMemoryCacheOf(true))
            request = request.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
        } else {
            request = requestManager.load(url)
        }
        request = request.apply(RequestOptions.circleCropTransform())
        if (placeHolder != 0) {
            request = request.apply(RequestOptions.placeholderOf(placeHolder))
        }
        request.into(this)
    } else {
        loadImageResource(placeHolder, 0, 0)
    }
}

@BindingAdapter(value = ["greyScale"])
fun loadImageGreyScale(view: ImageView, greyScale: Boolean) {
    if (greyScale) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val cf = ColorMatrixColorFilter(matrix)
        view.colorFilter = cf
        view.imageAlpha = 128
    } else {
        val matrix = ColorMatrix()
        matrix.setSaturation(1f)
        val cf = ColorMatrixColorFilter(matrix)
        view.colorFilter = cf
        view.imageAlpha = 255
    }
}