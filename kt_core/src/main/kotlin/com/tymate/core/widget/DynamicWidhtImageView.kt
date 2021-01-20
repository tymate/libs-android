package com.tymate.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.tymate.core.ui.R

open class DynamicWidhtImageView : AppCompatImageView {

    private var ratio = 0.0f
    private var wantedWidth = 0

    val widthRatio: Double
        get() = ratio.toDouble()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        widthRatioFromAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        widthRatioFromAttributes(context, attrs)
    }

    private fun widthRatioFromAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DynamicWidhtImageView)
        ratio = a.getFloat(R.styleable.DynamicWidhtImageView_ratio, 0f)
        a.recycle()
    }

    fun setWidthRatio(ratio: Float) {
        if (ratio != this.ratio) {
            this.ratio = ratio
            requestLayout()
        }
    }

    fun setWidth(width: Int) {
        if (width != wantedWidth) {
            wantedWidth = width
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio > 0.0) {
            // set the image views size
            val height = View.MeasureSpec.getSize(heightMeasureSpec)
            val width = (height * ratio).toInt()
            setMeasuredDimension(width, height)
        } else if (wantedWidth != 0) {
            val height = View.MeasureSpec.getSize(heightMeasureSpec)
            val width = wantedWidth
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
