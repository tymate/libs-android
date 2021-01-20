package com.tymate.core.adapter


import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */

open class BindableHolder<T>(
    val adapter: RecyclerAdapter,
    val binder: BaseBinder<T>,
    view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    private val CLICK_TIME_INTERVAL: Long = 500
    private var lastClickTime = 0L

    private var onItemClickListener: OnItemClickListener<T>? = null
    private var onItemLongClickListener: OnItemLongClickListener<T>? = null

    protected val clickableView: View?
        get() = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        enableClick()
        this.onItemClickListener = onItemClickListener
    }

    fun enableClick() {
        val view = clickableView
        if (view != null) {
            view.setOnClickListener(this)
        } else {
            itemView.setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {
        val adapterPosition = adapterPosition
        if (adapterPosition == -1) {
            return
        }
        val now = System.currentTimeMillis()
        if (now - lastClickTime < CLICK_TIME_INTERVAL) {
            return
        }
        lastClickTime = now
        onClick(view, adapterPosition)
    }

    open fun onClick(view: View, position: Int) {
        onClick(view, position, adapter.getItem(position) as T)
    }

    open fun onClick(view: View, position: Int, item: T) {
        onItemClickListener?.onItemClick(view, item, position, adapter)
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener<T>) {
        enableLongClick()
        this.onItemLongClickListener = onItemLongClickListener
    }

    fun enableLongClick() {
        val view = clickableView
        if (view != null) {
            view.setOnClickListener(this)
        } else {
            itemView.setOnClickListener(this)
        }
    }

    override fun onLongClick(view: View): Boolean {
        val adapterPosition = adapterPosition
        if (adapterPosition == -1) {
            return false
        }
        return onLongClick(view, adapterPosition)
    }

    open fun onLongClick(view: View, position: Int): Boolean {
        if (onItemLongClickListener == null) {
            return false
        }
        onItemLongClickListener?.onItemLongClick(view, adapter.getItem(adapterPosition) as T, position, adapter)
        return true
    }

    open fun bind(item: T, position: Int, adapter: RecyclerAdapter, payloads: MutableList<Any>) {
        binder.bind(this, item, position, adapter, payloads)
    }
}

open class BindingHolder<T>(
    adapter: RecyclerAdapter,
    binder: Binder<T>,
    val binding: ViewDataBinding) : BindableHolder<T>(adapter, binder, binding.root) {

    override fun bind(item: T, position: Int, adapter: RecyclerAdapter, payloads: MutableList<Any>) {
        super.bind(item, position, adapter, payloads)
        binding.executePendingBindings()
    }
}

interface OnItemClickListener<T> {
    fun onItemClick(view: View, item: T, position: Int, adapter: RecyclerAdapter)
}

interface OnItemLongClickListener<T> {
    fun onItemLongClick(view: View, item: T, position: Int, adapter: RecyclerAdapter)
}
