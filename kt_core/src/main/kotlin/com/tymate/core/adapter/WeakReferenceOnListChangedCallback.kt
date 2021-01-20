package com.tymate.core.adapter

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView

import java.lang.ref.WeakReference

class WeakReferenceOnListChangedCallback<T>(adapter: RecyclerView.Adapter<*>) : ObservableList.OnListChangedCallback<ObservableList<T>>() {

    private val adapterReference: WeakReference<RecyclerView.Adapter<*>> = WeakReference(adapter)

    override fun onChanged(sender: ObservableList<T>) {
        adapterReference.get()?.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapterReference.get()?.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapterReference.get()?.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapterReference.get()?.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapterReference.get()?.notifyItemRangeRemoved(positionStart, itemCount)
    }
}