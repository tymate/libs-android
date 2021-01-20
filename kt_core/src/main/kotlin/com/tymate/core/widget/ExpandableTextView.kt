package com.tymate.core.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView


import androidx.appcompat.widget.AppCompatTextView
import androidx.core.util.Consumer
import androidx.databinding.BindingAdapter
import com.tymate.core.ui.R
import com.tymate.core.util.isEllipsized

/**
 * Copyright (C) 2016 Cliff Ophalvens (Blogc.at)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Cliff Ophalvens (Blogc.at)
 */
class ExpandableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatTextView(context, attrs, defStyle) {

    /**
     * Returns the [OnExpandListener].
     *
     * @return the listener.
     */
    /**
     * Sets a listener which receives updates about this [ExpandableTextView].
     *
     * @param onExpandListener the listener.
     */
    var onExpandListener: OnExpandListener? = null
    /**
     * Returns the current [TimeInterpolator] for expanding.
     *
     * @return the current interpolator, null by default.
     */
    /**
     * Sets a [TimeInterpolator] for expanding.
     *
     * @param expandInterpolator the interpolator
     */
    var expandInterpolator: TimeInterpolator? = null
    /**
     * Returns the current [TimeInterpolator] for collapsing.
     *
     * @return the current interpolator, null by default.
     */
    /**
     * Sets a [TimeInterpolator] for collpasing.
     *
     * @param collapseInterpolator the interpolator
     */
    var collapseInterpolator: TimeInterpolator? = null

    private val maxLines: Int
    private var animationDuration: Long = 150
    private var animating: Boolean = false
    /**
     * Is this [ExpandableTextView] expanded or not?
     *
     * @return true if expanded, false if collapsed.
     */
    var isExpanded: Boolean = false
        private set
    private var collapsedHeight: Int = 0

    init {

        // read attributes
        //        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyle, 0);
        //        this.animationDuration = attributes.getInt(R.styleable.ExpandableTextView_animation_duration, 250);
        //        attributes.recycle();

        // keep the original value of maxLines
        this.maxLines = this.getMaxLines()

        // create default interpolators
        this.expandInterpolator = AccelerateDecelerateInterpolator()
        this.collapseInterpolator = AccelerateDecelerateInterpolator()
    }

    override fun getMaxLines(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return super.getMaxLines()
        }

        try {
            val mMaxMode = TextView::class.java.getField("mMaxMode")
            mMaxMode.isAccessible = true
            val mMaximum = TextView::class.java.getField("mMaximum")
            mMaximum.isAccessible = true

            val mMaxModeValue = mMaxMode.get(this) as Int
            val mMaximumValue = mMaximum.get(this) as Int

            return if (mMaxModeValue == MAXMODE_LINES) mMaximumValue else -1
        } catch (e: Exception) {
            return -1
        }

    }

    /**
     * Toggle the expanded reference of this [ExpandableTextView].
     *
     * @return true if toggled, false otherwise.
     */
    fun toggle(): Boolean {
        return if (this.isExpanded)
            this.collapse()
        else
            this.expand()
    }

    /**
     * Expand this [ExpandableTextView].
     *
     * @return true if expanded, false otherwise.
     */
    fun expand(): Boolean {
        if (!this.isExpanded && !this.animating && this.maxLines >= 0) {
            this.animating = true

            // notify listener
            if (this.onExpandListener != null) {
                this.onExpandListener!!.onExpand(this)
            }

            // get collapsed height
            this.measure(
                    View.MeasureSpec
                            .makeMeasureSpec(this.measuredWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            this.collapsedHeight = this.measuredHeight

            // set maxLines to MAX Integer, so we can calculate the expanded height
            this.setMaxLines(Integer.MAX_VALUE)

            // get expanded height
            this.measure(
                    View.MeasureSpec
                            .makeMeasureSpec(this.measuredWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val expandedHeight = this.measuredHeight

            // animate from collapsed height to expanded height
            val valueAnimator = ValueAnimator
                    .ofInt(this.collapsedHeight, expandedHeight)
            valueAnimator.addUpdateListener { animation ->
                val layoutParams = this@ExpandableTextView
                        .layoutParams
                layoutParams.height = animation.animatedValue as Int
                this@ExpandableTextView.layoutParams = layoutParams
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // if fully expanded, set height to WRAP_CONTENT, because when rotating the device
                    // the height calculated with this ValueAnimator isn't correct anymore
                    val layoutParams = this@ExpandableTextView
                            .layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    this@ExpandableTextView.layoutParams = layoutParams

                    // keep track of current role
                    this@ExpandableTextView.isExpanded = true
                    this@ExpandableTextView.animating = false
                }
            })

            // set interpolator
            valueAnimator.interpolator = this.expandInterpolator

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start()

            return true
        }

        return false
    }

    /**
     * Collapse this [TextView].
     *
     * @return true if collapsed, false otherwise.
     */
    fun collapse(): Boolean {
        if (this.isExpanded && !this.animating && this.maxLines >= 0) {
            this.animating = true

            // notify listener
            if (this.onExpandListener != null) {
                this.onExpandListener!!.onCollapse(this)
            }

            // get expanded height
            val expandedHeight = this.measuredHeight

            // animate from expanded height to collapsed height
            val valueAnimator = ValueAnimator
                    .ofInt(expandedHeight, this.collapsedHeight)
            valueAnimator.addUpdateListener { animation ->
                val layoutParams = this@ExpandableTextView
                        .layoutParams
                layoutParams.height = animation.animatedValue as Int
                this@ExpandableTextView.layoutParams = layoutParams
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // set maxLines to original value
                    this@ExpandableTextView.setMaxLines(this@ExpandableTextView.maxLines)

                    // if fully collapsed, set height to WRAP_CONTENT, because when rotating the device
                    // the height calculated with this ValueAnimator isn't correct anymore
                    val layoutParams = this@ExpandableTextView
                            .layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    this@ExpandableTextView.layoutParams = layoutParams

                    // keep track of current role
                    this@ExpandableTextView.isExpanded = false
                    this@ExpandableTextView.animating = false
                }
            })

            // set interpolator
            valueAnimator.interpolator = this.collapseInterpolator

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start()

            return true
        }

        return false
    }

    /**
     * Sets the duration of the expand / collapse animation.
     *
     * @param animationDuration duration in milliseconds.
     */
    fun setAnimationDuration(animationDuration: Long) {
        this.animationDuration = animationDuration
    }

    /**
     * Sets a [TimeInterpolator] for expanding and collapsing.
     *
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: TimeInterpolator) {
        this.expandInterpolator = interpolator
        this.collapseInterpolator = interpolator
    }

    /**
     * Interface definition for a callback to be invoked when
     * a [ExpandableTextView] is expanded or collapsed.
     */
    interface OnExpandListener {
        /**
         * The [ExpandableTextView] is being expanded.
         *
         * @param view the textview
         */
        fun onExpand(view: ExpandableTextView)

        /**
         * The [ExpandableTextView] is being collapsed.
         *
         * @param view the textview
         */
        fun onCollapse(view: ExpandableTextView)
    }

    companion object {
        // copy off TextView.LINES
        private val MAXMODE_LINES = 1
    }
}

@BindingAdapter(value = ["linkedExpandButton", "withBottomPadding"], requireAll = false)
fun isEllipsized(textView: TextView, linkedButton: View, withBottomPadding: Boolean) {
    textView.isEllipsized(Consumer { ellipsized ->
        linkedButton.visibility = if (ellipsized) View.VISIBLE else View.GONE
        with(textView) {
            if (withBottomPadding) {
                if (ellipsized) {
                    setPadding(paddingLeft, paddingTop, paddingRight, 0)
                } else {
                    setPadding(paddingLeft, paddingTop, paddingRight, resources.getDimensionPixelSize(R.dimen.global_margin))
                }
            }
        }
    })
}