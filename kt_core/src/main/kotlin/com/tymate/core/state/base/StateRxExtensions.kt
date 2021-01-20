package com.tymate.core.state.base

import androidx.annotation.CheckResult
import androidx.databinding.ObservableField
import com.tymate.core.state.ActionState
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Executors

object ReduceThread {
    val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
}

@CheckResult
fun <T, S, P> Observable<T>.load(
    field: ObservableField<S>,
    converter: (LoadEvent<T>) -> P,
    onPartial: ((P) -> Unit)? = null
): ResultDisposable<T> where S : ViewState<P>, P : PartialState {
    @Suppress("UNCHECKED_CAST")
    val partialLoad = converter(LoadEvent.Load())
    field.set(field.get()!!.reduce(partialLoad) as S)
    onPartial?.invoke(partialLoad)
    val resultDisposable = ResultDisposable<T>()
    resultDisposable.disposable = this
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .map { converter(LoadEvent.Success(data = it)) }
        .doOnError(Timber::w)
        .onErrorReturn { converter(LoadEvent.Error(it)) }
        .doOnNext { onPartial?.invoke(it) }
        .observeOn(ReduceThread.scheduler)
//        .scan(field.get()!!) { previousState: S, partialState: P ->
//            @Suppress("UNCHECKED_CAST")
//            val state = field.get()!!.reduce(partialState) as S
//            field.set(state)
//            state
//        }
        .map {
            val state = field.get()!!.reduce(it) as S
            field.set(state)
            state
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError = Timber::e, onNext = { state ->
            field.set(state)
            when (state) {
                is ActionState -> onPartial?.invoke(state.fromPartial as P)
                is State<*> -> onPartial?.invoke(state.fromPartial as P)
            }
        })
    return resultDisposable
}

@CheckResult
fun <T, S, P> Single<T>.load(
    field: ObservableField<S>,
    converter: (LoadEvent<T>) -> P
): ResultDisposable<T> where S : ViewState<P>, P : PartialState {
    @Suppress("UNCHECKED_CAST")
    field.set(field.get()!!.reduce(converter(LoadEvent.Load())) as S)
    val resultDisposable = ResultDisposable<T>()
    resultDisposable.disposable = this
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .map { converter(LoadEvent.Success(data = it)) }
        .doOnError(Timber::w)
        .onErrorReturn { converter(LoadEvent.Error(it)) }
        .observeOn(ReduceThread.scheduler)
        .map {
            val state = field.get()!!.reduce(it) as S
            field.set(state)
            state
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(onError = Timber::e, onSuccess = { state ->

            if (state is ActionState) {
                when (state.fromPartial) {
                    is ActionState.Partial.Failure ->
                        resultDisposable.onError?.invoke(state.fromPartial.throwable)
                    is ActionState.Partial.Success ->
                        resultDisposable.onSuccess?.invoke(state.fromPartial.response as T)
                }
            } else if (state is State<*>) {
                when (state.fromPartial) {
                    is State.Partial.LocalSuccess ->
                        resultDisposable.onSuccess?.invoke((state.fromPartial as State.Partial.LocalSuccess).data as T)
                    is State.Partial.LocalError ->
                        resultDisposable.onError?.invoke((state.fromPartial as State.Partial.LocalError).error)
                    is State.Partial.RemoteSuccess ->
                        resultDisposable.onSuccess?.invoke((state.fromPartial as State.Partial.RemoteSuccess).data as T)
                    is State.Partial.RemoteError -> {
                        resultDisposable.onError?.invoke((state.fromPartial as State.Partial.RemoteError).error)
                    }
                }
            }
        })
    return resultDisposable
}



class ResultDisposable<T> : Disposable {

    internal var disposable: Disposable? = null
    internal var onSuccess: ((T) -> Unit)? = null
    internal var onError: ((Throwable) -> Unit)? = null

    override fun isDisposed(): Boolean {
        return disposable?.isDisposed == true
    }

    override fun dispose() {
        disposable?.dispose()
    }

    fun onSuccess(onSuccess: ((T) -> Unit)): ResultDisposable<T> {
        this.onSuccess = onSuccess
        return this
    }

    fun onError(onError: ((Throwable) -> Unit)): ResultDisposable<T> {
        this.onError = onError
        return this
    }
}


sealed class LoadEvent<T> {
    class Load<T> : LoadEvent<T>()
    class Success<T>(val data: T) : LoadEvent<T>()
    class Error<T>(val error: Throwable) : LoadEvent<T>()
}