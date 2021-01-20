package com.tymate.core.util

import android.text.Layout
import android.view.ViewTreeObserver
import android.widget.TextView

import androidx.core.util.Consumer

fun TextView.isEllipsized(listener: Consumer<Boolean>) {
    requestLayout()
    val layout = layout
    if (layout != null) {
        try {
            listener.accept(layout.isEllipsized())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                try {
                    listener.accept(this@isEllipsized.layout.isEllipsized())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }
}

fun Layout.isEllipsized(): Boolean {
    val lines = lineCount
    if (lines > 0) {
        if (getEllipsisCount(lines - 1) > 0) {
            return true
        }
    }
    return false
}
