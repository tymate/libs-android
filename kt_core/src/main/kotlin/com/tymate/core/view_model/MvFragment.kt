package com.tymate.core.view_model

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM : ViewModel> Fragment.providedViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
                                                               crossinline viewModelProvider: () -> (ViewModelProvider)) = lazy(mode) {
    val vm = viewModelProvider().get(VM::class.java)
    if (vm is LifecycleObserver) {
        lifecycle.addObserver(vm)
    }
    vm
}

inline fun <reified VM : ViewModel> Fragment.fragmentViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
                                                               crossinline factoryProvider: () -> (ViewModelProvider.Factory)) = lazy(mode) {
    val vm = ViewModelProviders.of(this, factoryProvider()).get(VM::class.java)
    if (vm is LifecycleObserver) {
        lifecycle.addObserver(vm)
    }
    vm
}

inline fun <reified VM : ViewModel> Fragment.simpleFragmentViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) = lazy(mode) {
    val vm = ViewModelProviders.of(this).get(VM::class.java)
    if (vm is LifecycleObserver) {
        lifecycle.addObserver(vm)
    }
    vm
}
inline fun <reified VM : ViewModel> Fragment.activityViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
                                                               crossinline factoryProvider: () -> (ViewModelProvider.Factory)) = lazy(mode) {
    ViewModelProviders.of(activity!!, factoryProvider()).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Fragment.simpleActivityViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) = lazy(mode) {
    ViewModelProviders.of(activity!!).get(VM::class.java)
}
