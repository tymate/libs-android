package com.tymate.core.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import io.reactivex.Observable

fun Activity.showKeyboard() {
    currentFocus?.showKeyboard()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val binder = this.windowToken
    if (binder != null) {
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun Activity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}

fun EditText.showKeyboard() {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            this.post { showKeyboard() }
        }
    }
    this.post { this.requestFocus() }
    this.setSelection(this.text.toString().length)
}

/**
 * Observable of the status of the keyboard. Subscribing to this creates a
 * Global Layout Listener which is automatically removed when this
 * observable is disposed.
 */
val Activity.keyboardStatus: Observable<KeyboardStatus>
    get() {
        return Observable.create<KeyboardStatus> { emitter ->
            val rootView = this.findViewById<View>(android.R.id.content)

            // why are we using a global layout listener? Surely Android
            // has callback for when the keyboard is open or closed? Surely
            // Android at least lets you query the status of the keyboard?
            // Nope! https://stackoverflow.com/questions/4745988/
            val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {

                val rect = Rect().apply { rootView.getWindowVisibleDisplayFrame(this) }

                val screenHeight = rootView.height

                // rect.bottom is the position above soft keypad or device button.
                // if keypad is shown, the rect.bottom is smaller than that before.
                val keypadHeight = screenHeight - rect.bottom

                // 0.15 ratio is perhaps enough to determine keypad height.
                if (keypadHeight > screenHeight * 0.15) {
                    emitter.onNext(KeyboardStatus.OPEN)
                } else {
                    emitter.onNext(KeyboardStatus.CLOSED)
                }
            }

            rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

            emitter.setCancellable {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            }

        }.distinctUntilChanged()
    }

enum class KeyboardStatus {
    OPEN, CLOSED
}