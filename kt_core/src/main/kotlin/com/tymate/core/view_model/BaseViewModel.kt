package com.tymate.core.view_model

import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.tymate.core.error.ErrorManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class BaseViewModel : ViewModel(), LifecycleObserver, Disposable {

    var isFirstStarted = false
    var isStopped = false

    internal val coldDisposables: CompositeDisposable = CompositeDisposable()
    internal val hotDisposables: CompositeDisposable = CompositeDisposable()

    open fun onFirstStart() {

    }

    open fun onResume() {

    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
        Timber.i("onStart ${this}")
        val isFirstStart = !isFirstStarted
        isFirstStarted = true
        isStopped = false
        hotDisposables.clear()
        if (isFirstStart) {
            onFirstStart()
        } else {
            onResume()
        }
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
        Timber.i("onStop ${this} with hotDisposable.size = ${hotDisposables.size()}")
        isStopped = true
        hotDisposables.clear()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared ${this}")
        dispose()
    }

    override fun isDisposed(): Boolean {
        return hotDisposables.isDisposed && coldDisposables.isDisposed
    }

    override fun dispose() {
        Timber.i("dispose ${this}")
        coldDisposables.clear()
        hotDisposables.clear()
    }
}

fun Disposable.addTo(vm: BaseViewModel) {
    if (vm.isFirstStarted && !vm.isStopped) {
        Timber.i("addTo ${vm} with hotDisposable")
        addTo(vm.hotDisposables)
    } else {
        Timber.i("addTo ${vm} with coldDisposables")
        addTo(vm.coldDisposables)
    }
}

@BindingAdapter(value = ["errorText", "unknownErrorMessage"], requireAll = false)
fun TextView.setErrorText(throwable: Throwable?, unknownErrorMessage: Int) {
    if (throwable == null) {
        this.text = null
    } else {
        this.text = ErrorManager.from(context).handle(throwable, unknownErrorMessage).localizedMessage
    }
}