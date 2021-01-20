package com.tymate.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.tymate.core.ui.R

open class DynamicHeightImageView : AppCompatImageView {

    private var ratio = 0.0f
    private var wantedHeight = 0

    val heightRatio: Double
        get() = ratio.toDouble()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        heightRatioFromAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        heightRatioFromAttributes(context, attrs)
    }

    private fun heightRatioFromAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DynamicHeightImageView)
        ratio = a.getFloat(R.styleable.DynamicHeightImageView_ratio, 0f)
        a.recycle()
    }

    fun setHeightRatio(ratio: Float) {
        if (ratio != this.ratio) {
            this.ratio = ratio
            requestLayout()
        }
    }

    fun setHeight(height: Int) {
        if (height != wantedHeight) {
            wantedHeight = height
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio > 0.0) {
            // set the image views size
            val width = View.MeasureSpec.getSize(widthMeasureSpec)
            val height = (width * ratio).toInt()
            setMeasuredDimension(width, height)
        } else if (wantedHeight != 0) {
            val height = wantedHeight
            val width = View.MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
