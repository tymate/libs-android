package com.tymate.core.view_model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified VM> AppCompatActivity.activityViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
                                                            crossinline factory: () -> ViewModelProvider.Factory) where VM : ViewModel = lazy(mode) {
    val vm = ViewModelProviders.of(this, factory()).get(VM::class.java)
    if (vm is LifecycleObserver) {
        lifecycle.addObserver(vm)
    }
    vm
}

inline fun <reified VM> AppCompatActivity.simpleActivityViewModel(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) where VM : ViewModel = lazy(mode) {
    val vm = ViewModelProviders.of(this).get(VM::class.java)
    if (vm is LifecycleObserver) {
        lifecycle.addObserver(vm)
    }
    vm
}
//abstract class MvActivity<VB : ViewDataBinding> : BindingActivity<VB>() {
//    abstract fun viewModelFactory(): ViewModelProvider.Factory
//}

//inline fun <reified VM> MvActivity<*>.activityViewModel(kClass: KClass<VM>, mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) where VM : ViewModel = lazy(mode) {
//    val vm = ViewModelProviders.of(this, viewModelFactory()).get(kClass.java)
//    if (vm is LifecycleObserver) {
//        lifecycle.addObserver(vm)
//    }
//    vm
//}
//
//inline fun <reified VM> MvActivity<*>.viewModelProvider(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) where VM : ViewModel = lazy(mode) {
//    val vm = ViewModelProviders.of(this, viewModelFactory()).get(VM::class.java)
//    if (vm is LifecycleObserver) {
//        lifecycle.addObserver(vm)
//    }
//    vm
//}


//inline fun <reified VM : ViewModel> MvActivity<*>.viewModelProvider(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE, crossinline provider: () -> VM) = lazy(mode) {
//    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
//        override fun <T1 : ViewModel> create(aClass: Class<T1>) = provider() as T1
//    }).get(VM::class.java)
//}