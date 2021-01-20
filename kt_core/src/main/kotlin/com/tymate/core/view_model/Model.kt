package com.tymate.core.view_model

import androidx.databinding.BaseObservable

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
open class Model<T> : BaseObservable() {

    var it: T? = null
        private set

    @JvmOverloads
    open fun set(t: T?, notifyChange: Boolean = true) {
        this.it = t
        if (notifyChange) {
            notifyChange()
        }
    }

    fun clear(notifyChange: Boolean = true) {
        it = null
        if (notifyChange) {
            notifyChange()
        }
    }
}