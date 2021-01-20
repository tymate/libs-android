package com.tymate.core.state

import androidx.annotation.CheckResult
import androidx.databinding.ObservableField
import com.tymate.core.Optional
import com.tymate.core.state.base.LoadEvent
import com.tymate.core.state.base.ResultDisposable
import com.tymate.core.state.base.State
import com.tymate.core.state.base.load
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable


@CheckResult
fun <T> Observable<T>.loadRemote(
    field: ObservableField<DataState<T>>,
    onPartial: ((State.Partial<T>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.RemoteLoad()
            is LoadEvent.Success -> State.Partial.RemoteSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    }, onPartial = onPartial)
}


@CheckResult
fun <T> Single<T>.loadRemote(field: ObservableField<DataState<T>>): ResultDisposable<T> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.RemoteLoad()
            is LoadEvent.Success -> State.Partial.RemoteSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}

@CheckResult
fun <T> Observable<T>.loadLocal(
    field: ObservableField<DataState<T>>,
    onPartial: ((State.Partial<T>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.LocalLoad()
            is LoadEvent.Success -> State.Partial.LocalSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.LocalError(loadEvent.error)
        }
    }, onPartial = onPartial)
}


@CheckResult
fun <T> Single<T>.loadLocal(field: ObservableField<DataState<T>>): ResultDisposable<T> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.LocalLoad()
            is LoadEvent.Success -> State.Partial.LocalSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.LocalError(loadEvent.error)
        }
    })
}


@CheckResult
fun <T> Observable<Optional<T>>.loadLocalOptional(
    field: ObservableField<DataState<T?>>,
    onPartial: ((State.Partial<T?>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.LocalLoad()
            is LoadEvent.Success -> State.Partial.LocalSuccess(loadEvent.data.getNullable())
            is LoadEvent.Error -> State.Partial.LocalError(loadEvent.error)
        }
    }, onPartial = onPartial)
}

@CheckResult
fun <T> Single<Optional<T>>.loadLocalOptional(subject: ObservableField<DataState<T?>>): ResultDisposable<Optional<T>> {
    return load(subject, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.LocalLoad()
            is LoadEvent.Success -> State.Partial.LocalSuccess(loadEvent.data.getNullable())
            is LoadEvent.Error -> State.Partial.LocalError(loadEvent.error)
        }
    })
}
