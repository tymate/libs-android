package com.tymate.core.ui

import android.app.Dialog
import android.content.DialogInterface
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BindingBottomSheetDialogFragment<VB : ViewDataBinding> : BindingDialogFragment<VB>() {

    private val behaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            this@BindingBottomSheetDialogFragment.onStateChanged(bottomSheet, newState)
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            this@BindingBottomSheetDialogFragment.onSlide(bottomSheet, slideOffset)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener { dialog ->
            val behavior = getBehavior(view)
            behavior?.setBottomSheetCallback(behaviorCallback)
            onShow(dialog)
        }
    }

    fun setState(@BottomSheetBehavior.State state: Int) {
        val behavior = getBehavior(view)
        if (behavior != null) {
            behavior.state = state
        }
    }

    val behavior: CoordinatorLayout.Behavior<*>?
        get() = getBehavior(null)

    fun getBehavior(view: View?): BottomSheetBehavior<*>? {
        if (view == null) {
            return null
        }
        val coordinatorLayout = getParent(view)
        if (coordinatorLayout != null) {
            val params = coordinatorLayout.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            if (behavior is BottomSheetBehavior<*>) {
                return behavior
            }
        }
        val parent = view.parent
        return if (parent is View) {
            BottomSheetBehavior.from(parent as View)
        } else null
    }


    private fun getParent(view: View?): CoordinatorLayout? {
        if (view == null) {
            return null
        }
        val parent = view.parent as View
        return parent as? CoordinatorLayout
    }

    open fun onShow(dialog: DialogInterface) {

    }

    protected open fun onStateChanged(bottomSheet: View, newState: Int) {}

    protected open fun onSlide(bottomSheet: View, slideOffset: Float) {}
}
