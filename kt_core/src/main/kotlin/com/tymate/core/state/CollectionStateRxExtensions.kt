package com.tymate.core.state

import androidx.annotation.CheckResult
import androidx.databinding.ObservableField
import com.tymate.core.Paged
import com.tymate.core.state.base.LoadEvent
import com.tymate.core.state.base.ResultDisposable
import com.tymate.core.state.base.State
import com.tymate.core.state.base.load
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable


@CheckResult
fun <T> Observable<List<T>>.loadRemote(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
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
fun <T> Single<List<T>>.loadRemote(field: ObservableField<CollectionState<T>>): ResultDisposable<List<T>> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.RemoteLoad()
            is LoadEvent.Success -> State.Partial.RemoteSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}

@CheckResult
fun <T> Observable<List<T>>.refresh(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.Reset()
            is LoadEvent.Success -> State.Partial.RemoteSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    }, onPartial = onPartial)
}

@CheckResult
fun <T> Single<List<T>>.refresh(field: ObservableField<CollectionState<T>>): ResultDisposable<List<T>> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.Reset()
            is LoadEvent.Success -> State.Partial.RemoteSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}

@CheckResult
fun <T> Observable<List<T>>.loadLocal(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
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
fun <T> Single<List<T>>.loadLocal(field: ObservableField<CollectionState<T>>): ResultDisposable<List<T>> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.LocalLoad()
            is LoadEvent.Success -> State.Partial.LocalSuccess(loadEvent.data)
            is LoadEvent.Error -> State.Partial.LocalError(loadEvent.error)
        }
    })
}


@CheckResult
fun <T> Observable<Paged<List<T>>>.loadMore(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
): Disposable {
    val page = field.get()!!.currentPage + 1
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> CollectionState.Partial.LoadMore()
            is LoadEvent.Success -> CollectionState.Partial.PageSuccess(
                loadEvent.data,
                page
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    }, onPartial = onPartial)
}


@CheckResult
fun <T> Single<Paged<List<T>>>.loadMore(field: ObservableField<CollectionState<T>>): ResultDisposable<Paged<List<T>>> {
    val page = field.get()!!.currentPage + 1
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> CollectionState.Partial.LoadMore()
            is LoadEvent.Success -> CollectionState.Partial.PageSuccess(
                loadEvent.data,
                page
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}

@CheckResult
fun <T> Observable<Paged<List<T>>>.pullData(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.PullLoad()
            is LoadEvent.Success -> CollectionState.Partial.PullSuccess(
                loadEvent.data
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    }, onPartial = onPartial)
}

@CheckResult
fun <T> Single<Paged<List<T>>>.pullData(field: ObservableField<CollectionState<T>>): ResultDisposable<Paged<List<T>>> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.PullLoad()
            is LoadEvent.Success -> CollectionState.Partial.PullSuccess(
                loadEvent.data
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}

@CheckResult
fun <T> Observable<Paged<List<T>>>.refreshPaged(
    field: ObservableField<CollectionState<T>>,
    onPartial: ((State.Partial<List<T>>) -> Unit)? = null
): Disposable {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.RemoteLoad()
            is LoadEvent.Success -> CollectionState.Partial.PullSuccess(
                loadEvent.data
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    }, onPartial = onPartial)
}

@CheckResult
fun <T> Single<Paged<List<T>>>.refreshPaged(field: ObservableField<CollectionState<T>>): ResultDisposable<Paged<List<T>>> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> State.Partial.RemoteLoad()
            is LoadEvent.Success -> CollectionState.Partial.PullSuccess(
                loadEvent.data
            )
            is LoadEvent.Error -> State.Partial.RemoteError(loadEvent.error)
        }
    })
}
