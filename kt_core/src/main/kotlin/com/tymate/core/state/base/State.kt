package com.tymate.core.state.base

import androidx.databinding.ObservableField

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
interface State<T> : ViewState<State.Partial<T>> {

    val data: T?
    val localLoading: Boolean
    val localEnabled: Boolean
    val remoteEnabled: Boolean
    val remoteLoading: Boolean
    val refreshLoading: Boolean
    val error: Throwable?

    val fromPartial: State.Partial<T>?

    val globalLoading: Boolean
        get() = dataEmpty && (remoteLoading || localLoading)

    val dataEmpty: Boolean
        get() = isDataEmpty(data)

    val isEmpty: Boolean
        get() = dataEmpty && !(remoteLoading || localLoading)

    val globalError: Throwable?
        get() {
            if (!dataEmpty) {
                return null
            } else if (remoteLoading || localLoading) {
                return null
            }
            return error
        }

    val hintError: Throwable?
        get() = if (dataEmpty) null else error

    val loading: Boolean
        get() = remoteLoading || localLoading || refreshLoading

    fun isDataEmpty(data: T?): Boolean

    interface Partial<T> : PartialState {
        class LocalLoad<T> : Partial<T>
        class RemoteLoad<T> : Partial<T>
        class LocalSuccess<T>(val data: T) : Partial<T>
        class LocalError<T>(val error: Throwable) : Partial<T>
        class RemoteSuccess<T>(val data: T) : Partial<T>
        class RemoteError<T>(val error: Throwable) : Partial<T>
        class PullLoad<T> : Partial<T>
        class Reset<T> : Partial<T>
        class Empty<T> : Partial<T>
    }
}


fun <S : State<O>, O, P: State.Partial<O>> ObservableField<S>.reduce(partialState: P) {
    get()?.let { set(it.reduce(partialState) as S) }
}