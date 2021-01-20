package com.tymate.core.error

import android.content.Context
import androidx.annotation.StringRes
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

open class ErrorManager private constructor(private val context: Context) {

    private val resources = context.resources


//    private fun <X> Throwable?.isKindOf(kClass: KClass<X>): Boolean where X : Any {
//        return getKindOf(kClass) != null
//    }
//
//    private fun <X> Throwable?.getKindOf(kClass: KClass<X>): X? where X : Any{
//        if (this == null) {
//            return null
//        }
//        if (kClass.isInstance()) {
//            return this as X
//        }
//        if (this.getKindOf2<X>() != null) {
//
//        }
//        return cause.getKindOf(kClass)
//    }
//
////    inline fun <reified T> Throwable?.getKindOf2(): T? = if (this == null) null else this as T
//
//    inline fun <reified T> Throwable?.getKindOf2(): T? = if (this is T) this else null

    @JvmOverloads
    fun handle(error: Throwable, @StringRes unknownErrorMessage: Int = 0): LocalizedError {
        error.getHttp()?.let {
            return getHttpMessage(it, unknownErrorMessage)
        }
        error.getIO()?.let {
            return getIOMessage(it)
        }
        return getGeneralError(error, 0)
    }

    fun code401(error: Throwable, @StringRes message: Int): LocalizedError {
        return LocalizedError.createHttpCode(
            resources,
            401,
            error,
            message
        )
    }

    fun code413(error: Throwable, @StringRes message: Int): LocalizedError {
        return LocalizedError.createHttpCode(
            resources,
            413,
            error,
            message
        )
    }

    private fun getHttpMessage(e: HttpException, @StringRes unknownErrorMessage: Int = 0): LocalizedError {
        return LocalizedError.createHttpCode(
            resources,
            e.code(),
            e,
            unknownErrorMessage
        )
    }

    private fun getIOMessage(e: IOException): LocalizedError {
        return if (!NetworkManager.isNetworkAvailable(context)) {
            LocalizedError.createIO(
                resources,
                ErrorType.ERROR_NOT_CONNECTED,
                e
            )
        } else if (e.isTimeOut()) {
            LocalizedError.createIO(
                resources,
                ErrorType.ERROR_TIME_OUT,
                e
            )
        } else {
            LocalizedError.createIO(
                resources,
                ErrorType.ERROR_NETWORK,
                e
            )
        }
    }

    private fun getGeneralError(throwable: Throwable, @StringRes stringRes: Int): LocalizedError {
        return LocalizedError.createGeneralError(resources, stringRes, throwable)
    }

    companion object {

        fun from(context: Context): ErrorManager {
            return ErrorManager(context)
        }

        fun getHttpException(throwable: Throwable?): HttpException? {
            if (throwable is HttpException) {
                return throwable
            } else if (throwable != null && throwable.cause is HttpException) {
                return throwable.cause as HttpException
            }
            return null
        }
    }
}

fun Throwable?.isTimeOut(): Boolean {
    return this is SocketTimeoutException || getTimeOut()?.let {
        it.message != null && (it.message!!.toLowerCase().contains("etimedout") || it.message!!.toLowerCase().contains("timeout"))
    } ?: false
}

fun Throwable?.isHttp(): Boolean {
    return getHttp() != null
}

fun Throwable?.isIO(): Boolean {
    return getIO() != null
}

fun Throwable?.getIO(): IOException? {
    if (this == null) {
        return null
    }
    if (this is IOException) {
        return this
    }
    return cause.getIO()
}

fun Throwable?.getHttp(): HttpException? {
    if (this == null) {
        return null
    }
    if (this is HttpException) {
        return this
    }
    return cause.getHttp()
}

fun Throwable?.getTimeOut(): SocketTimeoutException? {
    if (this == null) {
        return null
    }
    if (this is SocketTimeoutException) {
        return this
    }
    return cause.getTimeOut()
}

fun Throwable?.isInterrupted(): Boolean {
    if (this == null) {
        return false
    }
    if (this is InterruptedException || this is InterruptedIOException) {
        return true
    }
    return cause.isInterrupted()
}

val Throwable?.httpCode: Int?
    get() = getHttp()?.code()
