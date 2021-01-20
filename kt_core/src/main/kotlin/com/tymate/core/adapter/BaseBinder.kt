package com.tymate.core.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBinder<T>(val layoutId: Int) {

    var isLongClickEnabled = false
    var onItemClickListener: OnItemClickListener<T>? = null
    var onItemLongClickListener: OnItemLongClickListener<T>? = null

    open fun createHolder(adapter: RecyclerAdapter, inflater: LayoutInflater, parent: ViewGroup): BindableHolder<T> {
        return BindableHolder(adapter, this, inflater.inflate(layoutId, parent, false))
    }

    open fun isValid(item: Any): Boolean {
        return true
    }

    abstract fun bind(holder: BindableHolder<T>, item: T, position: Int, adapter: RecyclerAdapter, payloads: MutableList<Any>)

    open fun onItemClick(holder: BindableHolder<T>, view: View, item: T, position: Int, adapter: RecyclerAdapter) {
        onItemClickListener?.onItemClick(view, item, position, adapter)
    }

    open fun onItemLongClick(holder: BindableHolder<T>, view: View, item: T, position: Int, adapter: RecyclerAdapter) {
        onItemLongClickListener?.onItemLongClick(view, item, position, adapter)
    }
}

abstract class Binder<T>(layoutId: Int, val layoutVariable: Int = -1) : BaseBinder<T>(layoutId) {

    override fun createHolder(adapter: RecyclerAdapter, inflater: LayoutInflater, parent: ViewGroup): BindableHolder<T> {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                inflater,
                layoutId,
                parent,
                false
        )
        return createHolder(adapter, binding)
    }

    open fun createHolder(adapter: RecyclerAdapter, binding: ViewDataBinding): BindableHolder<T> {
        return BindingHolder(adapter, this, binding)
    }

    override fun bind(holder: BindableHolder<T>, item: T, position: Int, adapter: RecyclerAdapter, payloads: MutableList<Any>) {
        if (holder is BindingHolder<T>) {
            bind(holder, item, position, adapter, payloads)
        }
    }

    open fun bind(holder: BindingHolder<T>, item: T, position: Int, adapter: RecyclerAdapter, payloads: MutableList<Any>) {
        if (layoutVariable != -1) {
            holder.binding.setVariable(layoutVariable, item)
        }
    }
}

