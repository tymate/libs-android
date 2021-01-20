package com.tymate.core.databinding

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import com.tymate.core.util.touch.SingleTapTouch
import com.tymate.core.widget.OnSingleClickListener


@set:BindingAdapter("isVisible")
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }


@BindingAdapter("onTap")
fun View.onTap(onClick: View.OnClickListener?) {
    if (onClick == null) {
        setOnTouchListener(null)
    } else {
        setOnTouchListener(object : SingleTapTouch(context) {
            override fun onSingleTapConfirmed(event: MotionEvent) {
                onClick.onClick(this@onTap)
            }
        })
    }
}

@BindingAdapter("enableCascade")
fun View.setEnableCascade(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) {
        val maxIndex = this.childCount - 1
        for (index in 0..maxIndex) {
            this.getChildAt(index).setEnableCascade(enabled)
        }
    }
}

@BindingAdapter("layout_height")
fun View.setLayoutHeight(value: Int) {
    layoutParams.height = value
}

@BindingAdapter("onSingleClick")
fun View.setOnSingleClickListener(clickListener: View.OnClickListener?) {
    clickListener?.also {
        setOnClickListener(OnSingleClickListener(it))
    } ?: setOnClickListener(null)
}