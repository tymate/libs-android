package com.tymate.core.util.touch

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class SingleTapTouch(context: Context) : View.OnTouchListener,
    ClickItemTouchListener.SingleTapListener, View.OnClickListener {

    @Suppress("LeakingThis")
    private val detector = GestureDetector(context, ClickItemTouchListener(this))

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        return true
    }

    override fun onClick(p0: View?) {

    }

    override fun onSingleTapConfirmed(event: MotionEvent) {

    }
}
