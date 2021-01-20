package com.tymate.core.ui

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class BaseFragment : Fragment() {

    internal val coldDisposables: CompositeDisposable = CompositeDisposable()
    internal val hotDisposables: CompositeDisposable = CompositeDisposable()

    var isFirstStart = true
        private set
    var isStopped = false
        private set
    var isStarted = false
        private set

    val baseActivity: BaseActivity? get() = activity as BaseActivity?

    fun setTitle(title: CharSequence?) {
        baseActivity?.let {
            if (it.title != title) {
                it.title = title
            }
        }
    }

    fun setTitle(title: Int?) {
        baseActivity?.setTitle(title ?: 0)
    }

    override fun onStart() {
        super.onStart()
        isStopped = false
        isStarted = true
    }

    override fun onStop() {
        hotDisposables.clear()
        super.onStop()
        isFirstStart = false
        isStopped = true
        isStarted = false
    }

    override fun onDestroy() {
        coldDisposables.clear()
        super.onDestroy()
    }

}

fun Disposable.addTo(fragment: BaseFragment) {
    if (fragment.isStarted) {
        addTo(fragment.hotDisposables)
    } else {
        addTo(fragment.coldDisposables)
    }
}