package com.tymate.core.error

import android.content.res.Resources
import androidx.annotation.StringRes
import com.tymate.core.ui.R
import com.tymate.core.util.ResourceUtils
import timber.log.Timber

class LocalizedError constructor(var errorType: ErrorType, message: String, cause: Throwable?): Throwable(message, cause) {

    override fun toString(): String {
        return "errorType=$errorType, message=$message"
    }

    override fun getLocalizedMessage(): String {
        return message ?: "Unknown Error..."
    }

    companion object {

        fun createIO(resources: Resources,
                     errorType: ErrorType,
                     throwable: Throwable): LocalizedError {
            val message = when (errorType) {
                ErrorType.ERROR_NOT_CONNECTED -> resources.getString(R.string.error_network_connection)
                ErrorType.ERROR_TIME_OUT -> resources.getString(R.string.error_network_time_out)
                else -> resources.getString(R.string.error_network_problem)
            }
            return LocalizedError(errorType, message, throwable)
        }

        fun createHttpCode(resources: Resources,
                           httpCode: Int,
                           throwable: Throwable,
                           @StringRes genericError: Int): LocalizedError {
            val message: String = when {
                httpCode >= 500 -> {
                    Timber.e(throwable)
                    resources.getString(R.string.error_network_server_problem)
                }
                genericError != 0 -> resources.getString(genericError)
                else -> {
                    val id = ResourceUtils.getResId("error_server_${httpCode}_msg", R.string::class.java)
                    if (id != 0) {
                        resources.getString(id)
                    } else {
                        resources.getString(R.string.error_unknown)
                    }
                }
            }
            return LocalizedError(
                ErrorType.ERROR_SERVER,
                message,
                throwable
            )
        }

        fun createGeneralError(resources: Resources,
                               throwable: Throwable): LocalizedError {
            val message = resources.getString(R.string.error_unknown)
            return LocalizedError(
                ErrorType.ERROR_UNKNOWN,
                message,
                throwable
            )
        }

        fun createGeneralError(resources: Resources,
                               @StringRes stringRes: Int,
                               throwable: Throwable): LocalizedError {
            if (stringRes == 0) {
                return createGeneralError(resources, throwable)
            } else {
                return LocalizedError(
                    ErrorType.ERROR_UNKNOWN,
                    resources.getString(stringRes),
                    throwable
                )
            }
        }
    }
}