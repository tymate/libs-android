package com.tymate.core.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import com.tymate.core.error.ErrorManager
import com.tymate.core.error.LocalizedError
import timber.log.Timber

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class Toaster(context: Context) {

    private var toast: Toast? = null
    private val appContext: Context = context.applicationContext
    private val mainThread = Handler(Looper.getMainLooper())
    private val errorManager = ErrorManager.from(appContext)

    fun cancelToast() {
        if (toast != null) {
            toast!!.cancel()
        }
    }

    fun longToast(throwable: Throwable) {
        if (throwable is LocalizedError) {
            longToast(localizedError = throwable)
        } else {
            longToast(localizedError = errorManager.handle(throwable))
        }
    }

    private fun longToast(localizedError: LocalizedError?) {
        if (localizedError != null) {
            longToast(localizedError.localizedMessage)
        } else {
            Timber.e(RuntimeException("localized error == null"))
        }
    }

    fun longToast(text: String) {
        toast(text, Toast.LENGTH_LONG)
    }

    fun longToast(@StringRes text: Int) {
        toast(text, Toast.LENGTH_LONG)
    }

    fun shortToast(throwable: Throwable) {
        shortToast(localizedError = errorManager.handle(throwable))
    }

    fun shortToast(localizedError: LocalizedError?) {
        if (localizedError != null) {
            shortToast(localizedError.localizedMessage)
        } else {
            Timber.e(RuntimeException("localized error == null"))
        }
    }

    fun shortToast(text: String) {
        toast(text, Toast.LENGTH_SHORT)
    }

    fun shortToast(@StringRes text: Int) {
        toast(text, Toast.LENGTH_SHORT)
    }

    private fun toast(@StringRes text: Int, duration: Int) {
        toast(appContext.getString(text), duration)
    }

    private fun toast(text: String, duration: Int) {
        mainThread.post { showToast(text, duration) }
    }

    private fun showToast(text: String, duration: Int) {
        cancelToast()
        toast = Toast.makeText(appContext, text, duration)
        toast!!.show()
    }
}