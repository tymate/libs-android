package com.tymate.core.databinding

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.text.Html
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import timber.log.Timber


@BindingAdapter("textResources")
fun TextView.setTextResources(textResources: Int) {
    if (textResources == 0) {
        this.text = null
    } else {
        this.setText(textResources)
    }
}

@BindingAdapter("html")
fun TextView.setHtml(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        this.text = Html.fromHtml(html)
    }}

@BindingAdapter("actionDone")
fun TextView.bindActionDone(clickListener: View.OnClickListener?) {
    if (clickListener == null) {
        imeOptions = EditorInfo.IME_ACTION_NEXT
    } else {
        imeOptions = EditorInfo.IME_ACTION_DONE
    }
    setOnEditorActionListener { textView, actionId, keyEvent ->
        if (actionId == EditorInfo.IME_ACTION_DONE && clickListener != null) {
            clickListener.onClick(textView)
        }
        false
    }
}

private val DEFAULT_STATES = arrayOf(intArrayOf())

@BindingAdapter("supportDrawableTint")
fun TextView.tint(tint: Int) {
    for (drawable in compoundDrawables) {
        try {
            drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, tint), PorterDuff.Mode.SRC_IN)
        } catch (e: Exception) {
            Timber.w(e)
        }
    }
}

@BindingAdapter("supportBackgroundTint")
fun View.backgroundTint(tint: Int) {
    backgroundTint(
        ColorStateList(DEFAULT_STATES, intArrayOf(tint))
    )
}

@BindingAdapter("tint")
fun View.backgroundTint(tint: ColorStateList) {
    ViewCompat.setBackgroundTintList(this, tint)
}

@BindingAdapter("underline")
fun underline(textView: TextView, enabled: Boolean) {
    if (enabled) {
        underLineTextView(textView)
    }
}

private fun underLineTextView(textView: TextView) {
    textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}