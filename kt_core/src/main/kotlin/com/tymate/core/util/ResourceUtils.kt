package com.tymate.core.util

import timber.log.Timber

object ResourceUtils {
    fun getResId(variableName: String, c: Class<*>): Int {
        try {
            val idField = c.getDeclaredField(variableName)
            return idField.getInt(idField)
        } catch (e: Exception) {
            Timber.w("No resource ID found for name %s and class %s", variableName, c)
            return 0
        }
    }
}
