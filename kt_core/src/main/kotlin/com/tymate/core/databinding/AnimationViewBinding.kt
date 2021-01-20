package com.tymate.core.databinding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter(value = ["animationFade", "animationDuration", "visibilityFillAfter"], requireAll = false)
fun View.fade(visible: Boolean, duration: Int, visibilityFillAfter: Boolean) {
    fadeAnimation(visible, duration, visibilityFillAfter)
}

private fun View.fadeAnimation(show: Boolean, duration: Int, visibilityFillAfter: Boolean): Animator {
    val animator = AnimatorSet()
    animator.play(ObjectAnimator.ofFloat(this, View.ALPHA, if (show) 1f else 0f))
    animator.duration = if (duration == 0) 250 else duration.toLong()
    if (visibilityFillAfter) {
        if (show) {
            visibility = View.VISIBLE
        } else {
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                    animation?.removeListener(this)
                }
            })
        }
    }
    animator.start()
    return animator
}


@BindingAdapter("animationShakeError")
fun View.shakeError(shake: Boolean) {
    val animation = animation
    if (shake && animation == null) {
        shakeView()
    } else if (animation != null) {
        animation.cancel()
        this.animation = null
    }
}

private fun View.shakeView(): Animator {
    val animator = AnimatorSet()
    animator.playTogether(ObjectAnimator.ofFloat(this, View.TRANSLATION_X,
            0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f))
    animator.duration = 1000
    animator.start()
    return animator
}