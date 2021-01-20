package com.tymate.core.view_model

import androidx.databinding.BaseObservable
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class BindableProperty<T>(
    initialValue: T,
    private val viewModel: ObservableViewModel,
    private val ids: IntArray,
    private val afterChange: ((oldValue: T, newValue: T) -> Unit)? = null
): ObservableProperty<T>(initialValue) {

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        return oldValue != newValue
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        afterChange?.invoke(oldValue, newValue)
        ids.forEach(viewModel::notifyPropertyChanged)
    }
}

fun <T> ObservableViewModel.bindable(initialValue: T, vararg ids: Int, afterChange: ((oldValue: T, newValue: T) -> Unit)? = null): BindableProperty<T> {
    return BindableProperty(initialValue, this, ids, afterChange)
}

class BindableProperty2<T>(
        initialValue: T,
        private val viewModel: BaseObservable,
        private val ids: IntArray,
        private val afterChange: ((oldValue: T, newValue: T) -> Unit)? = null
): ObservableProperty<T>(initialValue) {

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        return oldValue != newValue
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        afterChange?.invoke(oldValue, newValue)
        ids.forEach(viewModel::notifyPropertyChanged)
    }
}

fun <T> BaseObservable.bindable(initialValue: T, vararg ids: Int, afterChange: ((oldValue: T, newValue: T) -> Unit)? = null): BindableProperty2<T> {
    return BindableProperty2(initialValue, this, ids, afterChange)
}
