package com.tymate.core.databinding

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.tymate.core.Equatable
import com.tymate.core.adapter.BaseBinder
import com.tymate.core.adapter.LoaderBinder
import com.tymate.core.adapter.RecyclerAdapter
import com.tymate.core.state.CollectionState
import timber.log.Timber


fun RecyclerView.initBinders(vararg binders: BaseBinder<*>): RecyclerAdapter {
    var viewAdapter = adapter as? RecyclerAdapter
    val mutableList = binders.asList().toMutableList()
    mutableList.add(LoaderBinder())
    if (viewAdapter != null) {
        viewAdapter.setBinders(mutableList.map { it as BaseBinder<Any> })
    } else {
        viewAdapter =
            RecyclerAdapter(mutableList.map { it as BaseBinder<Any> })
        adapter = viewAdapter
    }
    return viewAdapter
}


//@BindingAdapter(value = ["controller", "diffUtilEnabled"], requireAll = false)
//fun <T> setRecyclerViewItems(view: RecyclerView, controller: PagedCollectionController<T>?, diffUtilEnabled: Boolean) {
//    val viewAdapter = view.adapter as? BinderRecyclerAdapter ?: throw RuntimeException()
//    val oldList = viewAdapter.getItemList()
//    val list = ObservableArrayList<Any>()
//    list.addAll(controller?.state?.get()?.data ?: arrayListOf())
//    viewAdapter.setItemList(list)
//    controller?.let {
//        view.removeOnScrollListener(it)
//        view.addOnScrollListener(it)
//    }
//    if (diffUtilEnabled) {
//        dispatchChanges(viewAdapter, oldList, list)
//    } else {
//        viewAdapter.notifyDataSetChanged()
//    }
//}

@BindingAdapter(value = ["list", "diffUtilEnabled", "clearOldData"], requireAll = false)
fun RecyclerView.setRecyclerViewItems(
    list: List<Any>?,
    diffUtilEnabled: Boolean,
    clearOldData: Boolean
) {
    val viewAdapter = adapter as? RecyclerAdapter ?: throw RuntimeException()
    val oldList = viewAdapter.currentList
    if (oldList is ObservableList && !clearOldData) {
        return
    }
    if (list == null) {
        return
    } else if (list is ObservableList) {
        viewAdapter.setList(list)
        viewAdapter.notifyDataSetChanged()
    } else {
        if (diffUtilEnabled) {
            viewAdapter.submitList(list, null, object : DiffUtil.ItemCallback<Any>() {
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return (oldItem as Equatable).areContentsTheSame(newItem as Equatable)
                }
            })
        } else {
            viewAdapter.setList(list)
            viewAdapter.notifyDataSetChanged()
        }
    }
}

@BindingAdapter(
    value = ["list", "diffUtilEnabled", "diffUtilCallback", "clearOldData"],
    requireAll = false
)
fun RecyclerView.setRecyclerViewItems(
    list: List<Any>?,
    diffUtilEnabled: Boolean,
    diffUtilCallback: DiffUtil.ItemCallback<Any>?,
    clearOldData: Boolean
) {
    val viewAdapter = adapter as? RecyclerAdapter ?: throw RuntimeException()
    val oldList = viewAdapter.currentList
    if (oldList is ObservableList && !clearOldData) {
        return
    }
    if (list == null) {
        return
    } else if (list is ObservableList) {
        viewAdapter.setList(list)
        viewAdapter.notifyDataSetChanged()
    } else {
        if (diffUtilEnabled) {
            if (diffUtilCallback == null) {
                viewAdapter.submitList(list, null, object : DiffUtil.ItemCallback<Any>() {
                    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                        return (oldItem as Equatable).areContentsTheSame(newItem as Equatable)
                    }
                })
            } else {
                viewAdapter.submitList(list, null, diffUtilCallback)
            }
        } else {
            viewAdapter.setList(list)
            viewAdapter.notifyDataSetChanged()
        }
    }
}


@BindingAdapter(value = ["state", "diffUtilEnabled"], requireAll = false)
fun <T> RecyclerView.setRecyclerViewItems(
    collectionState: ObservableField<CollectionState<T>>?,
    diffUtilEnabled: Boolean
) {
    val viewAdapter = adapter as? RecyclerAdapter ?: return
    val oldList = viewAdapter.currentList
    val list = ObservableArrayList<Any>()
    list.addAll(collectionState?.get()?.data ?: arrayListOf())
    Timber.i("setRecyclerViewItems with recycler $this and count ${list.size}")
    Timber.i(
        "setRecyclerViewItems with CollectionState address: ${System.identityHashCode(
            collectionState
        )}"
    )
    if (diffUtilEnabled) {
        viewAdapter.submitList(list, null, object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return (oldItem as Equatable).areContentsTheSame(newItem as Equatable)
            }
        })
    } else {
        viewAdapter.setList(list)
        viewAdapter.notifyDataSetChanged()
    }
}

@SuppressLint("ResourceType")
@BindingAdapter(value = ["divider"])
fun addDivider(view: RecyclerView, @DrawableRes value: Int) {
    if (value > 0) {
        view.addItemDecoration(DividerItemDecoration(view.context, value))
    }
}

@BindingAdapter(value = ["divider"])
fun addDivider(view: RecyclerView, drawable: Drawable?) {
    if (drawable != null) {

        val itemDecorator = DividerItemDecoration(
            view.context,
            DividerItemDecoration.VERTICAL
        )
        itemDecorator.setDrawable(drawable)
        view.addItemDecoration(itemDecorator)
    }
}

//@BindingAdapter(value = ["list", "diffUtilEnabled", "clearOldData"], requireAll = false)
//fun <T> setRecyclerViewItems(view: RecyclerView, list: List<T>?, diffUtilEnabled: Boolean, clearOldData: Boolean) {
//    val viewAdapter = view.adapter as? BinderRecyclerAdapter ?: throw RuntimeException()
//    val oldList = viewAdapter.getItemList()
//    if (viewAdapter.getItemList() is ObservableList && !clearOldData) {
//        return
//    }
//    if (list == null) {
//        return
//    } else if (list is ObservableList) {
//        viewAdapter.setItemList(list as ObservableList<Any>)
//        viewAdapter.notifyDataSetChanged()
//    } else {
//        viewAdapter.setItemList(list as List<Any>)
//        if (diffUtilEnabled) {
//            dispatchChanges(viewAdapter, oldList, list)
//        } else {
//            viewAdapter.notifyDataSetChanged()
//        }
//    }
//}

//@BindingAdapter(value = ["state", "diffUtilEnabled"], requireAll = false)
//fun <T> setRecyclerViewItems(view: RecyclerView, collectionState: ObservableField<CollectionState<T>>?, diffUtilEnabled: Boolean) {
//    val viewAdapter = view.adapter as? BinderRecyclerAdapter ?: throw RuntimeException()
//    val oldList = viewAdapter.getItemList()
//    val list = ObservableArrayList<Any>()
//    list.addAll(collectionState?.get()?.data ?: arrayListOf())
//    viewAdapter.setItemList(list)
//    Timber.i("setRecyclerViewItems with recycler $view and count ${list.size}")
//    Timber.i("setRecyclerViewItems with CollectionState address: ${System.identityHashCode(collectionState)}")
//
//    if (diffUtilEnabled) {
//        dispatchChanges(viewAdapter, oldList, list)
//    } else {
//        viewAdapter.notifyDataSetChanged()
//    }
//}

//fun dispatchChanges(adapter: BaseRecyclerAdapter<*>,
//                    oldList: List<*>,
//                    newList: List<*>) {
//    val oldSize = oldList.size
//    val newSize = newList.size
//
//    val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
//        override fun getOldListSize(): Int {
//            return oldSize
//        }
//
//        override fun getNewListSize(): Int {
//            return newSize
//        }
//
//        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return oldList[oldItemPosition] == newList[newItemPosition]
//        }
//
//        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            val oldItem = oldList[oldItemPosition] as Equatable
//            val newItem = newList[newItemPosition] as Equatable
//            return oldItem.areContentsTheSame(newItem)
//        }
//    }, true)
//    result.dispatchUpdatesTo(adapter)
//}