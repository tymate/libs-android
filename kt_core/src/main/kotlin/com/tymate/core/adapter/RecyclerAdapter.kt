package com.tymate.core.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class RecyclerAdapter(
    var adapterBinders: List<BaseBinder<Any>> = listOf()
) : RecyclerView.Adapter<BindableHolder<Any>>() {

    private val listChangedCallback by lazy { WeakReferenceOnListChangedCallback<Any>(this) }

    private val backgroundExecutors by lazy { Executors.newSingleThreadExecutor() }
    private val mainExecutor by lazy {
        val handler = Handler(Looper.getMainLooper())
        Executor {
            handler.post(it)
        }
    }

    public var currentList: MutableList<Any> = arrayListOf()
        private set

    open fun getItem(position: Int): Any {
        return currentList[position]
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun setBinders(adapterBinders: List<BaseBinder<Any>>?) {
        if (adapterBinders == null) {
            return
        }
        this.adapterBinders = adapterBinders
    }

    fun setBinders(vararg binders: BaseBinder<*>) {
        this.adapterBinders = binders.map { it as BaseBinder<Any> }
    }

    fun <T> setList(list: List<T>) {
        setList(list, true)
    }

    fun <T> setList(list: List<T>, notifyChange: Boolean) {
        removeOldListListener()
        if (list is ObservableList) {
            (list as ObservableList<Any>).addOnListChangedCallback(listChangedCallback)
            currentList = list
        } else {
            currentList = list.toMutableList() as MutableList<Any>
        }
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    /**
     * Submits a new list to be diffed, and displayed.
     *
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * @param list The new list to be displayed.
     */
    fun <T> submitList(
        list: List<T>,
        listener: BatchingListUpdateListener? = null,
        diffUtilCallback: DiffUtil.ItemCallback<T>
    ) {
        val oldList = currentList as List<T>
        dispatchChanges(oldList, list, listener, diffUtilCallback)
    }

    private fun getBinderByViewType(viewType: Int): BaseBinder<Any> {
        return adapterBinders.first { it.layoutId == viewType }
    }

    override fun getItemViewType(position: Int): Int {
        return getBinder(position).layoutId
    }

    private fun getBinder(position: Int): BaseBinder<Any> {
        return getBinder(getItem(position))
    }

    private fun getBinder(item: Any): BaseBinder<Any> {
        return adapterBinders.first { it.isValid(item) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableHolder<Any> {
        val binder = getBinderByViewType(viewType)
        val holder = getBinderByViewType(viewType).createHolder(this, LayoutInflater.from(parent.context), parent)
        holder.setOnItemClickListener(object : OnItemClickListener<Any> {
            override fun onItemClick(view: View, item: Any, position: Int, adapter: RecyclerAdapter) {
                binder.onItemClick(holder, view, item, position, adapter)
            }
        })
        if (binder.isLongClickEnabled) {
            holder.setOnItemLongClickListener(object : OnItemLongClickListener<Any> {
                override fun onItemLongClick(view: View, item: Any, position: Int, adapter: RecyclerAdapter) {
                    binder.onItemLongClick(holder, view, item, position, adapter)
                }
            })
        }
        return holder
    }

    override fun onBindViewHolder(holder: BindableHolder<Any>, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }
//    override fun onBindViewHolder(holder: BindableHolder<Any, *>, position: Int) {
//        onBindViewHolder(holder, position, mutableListOf())
//    }

    override fun onBindViewHolder(holder: BindableHolder<Any>, position: Int, payloads: MutableList<Any>) {
        holder.bind(getItem(position), position, this, payloads)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        removeOldListListener()
    }

    private fun removeOldListListener() {
        val list = currentList
        if (list is ObservableList<Any>) {
            list.removeOnListChangedCallback(listChangedCallback)
        }
    }

    private fun <T> dispatchChanges(
        oldList: List<T>,
        newList: List<T>,
        listener: BatchingListUpdateListener?,
        diffUtilCallback: DiffUtil.ItemCallback<T>
    ) {
        backgroundExecutors.execute {
            val oldSize = oldList.size
            val newSize = newList.size
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldSize
                }

                override fun getNewListSize(): Int {
                    return newSize
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffUtilCallback.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffUtilCallback.areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
                }
            }, true)

            mainExecutor.execute {
                setList(newList, false)
                result.dispatchUpdatesTo(AdapterListUpdateCallback(this))
                listener?.onLastEventDispatched()
            }
        }
    }
}

interface BatchingListUpdateListener {
    fun onLastEventDispatched()
}
