package com.tymate.core.util.touch

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class ClickItemTouchListener(private val singleTapListener: SingleTapListener?) : GestureDetector.SimpleOnGestureListener() {

    interface SingleTapListener {
        fun onSingleTapConfirmed(event: MotionEvent)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        singleTapListener?.onSingleTapConfirmed(e)
        return super.onSingleTapUp(e)
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
//        singleTapListener?.onSingleTapConfirmed(event)
        return true
    }
}