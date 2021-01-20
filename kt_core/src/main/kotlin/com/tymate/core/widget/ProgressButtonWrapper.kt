package com.tymate.core.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.tymate.core.ui.R
import timber.log.Timber

class ProgressButtonWrapper : FrameLayout {

    private val button: View? by lazy { findButton() }
    var isLoadingState = false

    fun findButton(): View? {
        return (0 until childCount)
                .map { getChildAt(it) }
                .firstOrNull { it is TextView || it is ImageView }
    }

    private val progressBar: View

    private var enabled = true
    private var text: String? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val res = context.resources

        progressBar = ProgressBar(context)
        progressBar.visibility = View.GONE
        val progressBarSize = res.getDimensionPixelSize(R.dimen.button_height)
        val params = FrameLayout.LayoutParams(progressBarSize, progressBarSize)
        params.gravity = Gravity.CENTER
        progressBar.layoutParams = params
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.elevation = res.getDimensionPixelSize(R.dimen.elevation).toFloat()
        }
        this.addView(progressBar)

    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        button?.isEnabled = enabled
    }

    fun setLoading(loading: Boolean) {
        Timber.i("Loading ? $loading")
        if (loading) {
            startProgress()
        } else {
            endProgress()
        }
        isLoadingState = loading
    }

    private fun startProgress() {
        if (isLoadingState) {
            return
        }
        progressBar.visibility = View.VISIBLE
        val button = button ?: return
        button.isEnabled = false
        button.isClickable = false
        if (button is TextView) {
            text = button.text.toString()
            button.text = ""
        }
    }

    private fun endProgress() {
        if (!isLoadingState) {
            return
        }
        progressBar.visibility = View.GONE
        val button = button ?: return
        button.isEnabled = enabled
        button.isClickable = true
        if (button is TextView) {
            button.text = text
        }
    }

    fun setText(text: String) {
        if (isLoadingState) {
            return
        }
        val button = button ?: return
        if (button is TextView) {
            button.text = text
        }
    }
}