package com.tymate.core.state

import androidx.databinding.ObservableField
import com.tymate.core.Paged
import com.tymate.core.state.base.State
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
data class CollectionState<T>(
    override val data: List<T> = arrayListOf(),
    override val localEnabled: Boolean = false,
    override val localLoading: Boolean = localEnabled,
    override val remoteEnabled: Boolean = false,
    override val remoteLoading: Boolean = remoteEnabled,
    override val refreshLoading: Boolean = false,
    override val error: Throwable? = null,
    override val fromPartial: State.Partial<List<T>>? = null,

    val paginationLoading: Boolean = false,
    val paginationEnabled: Boolean = false,
    val currentPage: Int = INITIAL_START_PAGE,
    val totalPages: Int = INITIAL_TOTAL_PAGE,
    val totalItems: Int = 0
) : State<List<T>> {


    override fun isDataEmpty(data: List<T>?): Boolean {
        return data!!.isEmpty()
    }

    fun canLoadMore(): Boolean {
        if (loading) {
            return false
        }
        if (currentPage >= totalPages) {
            return false
        }
        return true
    }

    fun <X> copy(list: List<X>): CollectionState<X> {
        return CollectionState(
            data = list,
            localEnabled = localEnabled,
            localLoading = localLoading,
            remoteEnabled = remoteEnabled,
            remoteLoading = remoteLoading,
            error = error,
            refreshLoading = refreshLoading,
            paginationEnabled = paginationEnabled,
            paginationLoading = paginationLoading,
            currentPage = currentPage,
            totalPages = totalPages,
            totalItems = totalItems
        )
    }

    override fun reduce(partialState: State.Partial<List<T>>): CollectionState<T> {
        Timber.i("reduce $partialState")
        return when (partialState) {
            is State.Partial.LocalLoad -> this.copy(localLoading = true, fromPartial = partialState)
            is State.Partial.RemoteLoad -> this.copy(remoteLoading = true, fromPartial = partialState)
            is State.Partial.PullLoad -> this.copy(refreshLoading = true, fromPartial = partialState)
            is Partial.LoadMore -> this.copy(
                remoteLoading = true,
                paginationLoading = true,
                error = null,
                fromPartial = partialState
            )

            is State.Partial.LocalSuccess -> {
                val partialData = partialState.data
                if (!isDataEmpty(partialData) || !remoteEnabled) {
                    this.copy(data = partialData, localLoading = false, fromPartial = partialState)
                } else {
                    this.copy(fromPartial = partialState)
                }
            }
            is State.Partial.RemoteSuccess -> {
                val partialData = partialState.data
                this.copy(
                    remoteLoading = false,
                    refreshLoading = false,
                    localLoading = localLoading && !partialData.isEmpty(),
                    data = if (localEnabled) data else partialData,
                    fromPartial = partialState
                )
            }

            is Partial.PageSuccess -> {

                val oldList = data.toMutableList()
                val newList = mutableListOf<T>()
                val newDatas = partialState.data
                var index: Int
                newDatas.forEach { newData ->
                    index = oldList.indexOf(newData)
                    if (index > -1) {
                        // replace old data with new
                        oldList[index] = newData
                    } else {
                        // add new data
                        newList.add(newData)
                    }
                }
                newList.addAll(index = 0, elements = oldList)
                this.copy(
                    paginationLoading = false,
                    remoteLoading = false,
                    currentPage = partialState.currentPage,
                    totalPages = partialState.totalPages,
                    totalItems = partialState.totalItems,
                    data = newList,
                    fromPartial = partialState
                )
            }
            is Partial.PullSuccess -> {
                val partialData = partialState.data
                val oldList = data.toMutableList()
                val newList = mutableListOf<T>()

                var index: Int
                partialData.forEach {
                    index = oldList.indexOf(it)
                    if (index > -1) {
                        oldList.removeAt(index)
                    }
                    newList.add(it)
                }
                newList.addAll(oldList)
                this.copy(
                    refreshLoading = false,
                    remoteLoading = false,
                    totalPages = partialState.totalPages,
                    totalItems = partialState.totalItems,
                    data = newList,
                    fromPartial = partialState
                )
            }


            is State.Partial.LocalError -> this.copy(
                error = partialState.error,
                localLoading = false,
                remoteLoading = false,
                data = if (dataEmpty) arrayListOf() else data,
                fromPartial = partialState
            )
            is State.Partial.RemoteError -> {
                if (localLoading && dataEmpty) {
                    this.copy(
                        remoteLoading = false,
                        error = partialState.error,
                        refreshLoading = false,
                        localLoading = false,
                        fromPartial = partialState
                    )
                } else {
                    this.copy(
                        remoteLoading = false,
                        error = partialState.error,
                        refreshLoading = false,
                        fromPartial = partialState
                    )
                }
            }


            is State.Partial.Reset -> this.copy(
                remoteLoading = remoteEnabled,
                localLoading = localEnabled,
                refreshLoading = false,
                currentPage = INITIAL_START_PAGE,
                totalPages = INITIAL_TOTAL_PAGE,
                totalItems = 0,
                error = null,
                data = arrayListOf(),
                fromPartial = partialState
            )
            is State.Partial.Empty -> this.copy(
                remoteLoading = false,
                localLoading = false,
                refreshLoading = false,
                paginationLoading = false,
                currentPage = INITIAL_START_PAGE,
                totalPages = INITIAL_TOTAL_PAGE,
                totalItems = 0,
                error = null,
                data = arrayListOf(),
                fromPartial = partialState
            )


            else -> this.copy()
        }.also { Timber.i("now is $it") }
    }


    interface Partial<T> : State.Partial<T> {

        class LoadMore<T> : State.Partial<List<T>>

        class PageSuccess<T> : State.Partial<List<T>> {
            var data: List<T>
            var localData: Boolean = false
            var currentPage: Int = 0
            var totalPages: Int = 0
            var totalItems: Int = 0

            constructor(data: Paged<List<T>>, page: Int) {
                this.localData = false
                this.data = data.data
                this.currentPage = page
                this.totalPages = data.totalPages
                this.totalItems = data.totalItems
            }
        }

        class PullSuccess<T> : State.Partial<List<T>> {
            var data: List<T>
            var totalPages: Int = 0
            var totalItems: Int = 0

            constructor(data: List<T>, totalPages: Int) {
                this.data = data
                this.totalPages = totalPages
            }

            constructor(data: Paged<List<T>>) {
                this.data = data.data
                this.totalPages = data.totalPages
                this.totalItems = data.totalItems
            }

            constructor(data: T) {
                val list = mutableListOf<T>()
                list.add(data)
                this.data = list
            }
        }
    }

    companion object {
        const val INITIAL_START_PAGE = 0
        const val INITIAL_TOTAL_PAGE = -1

        fun <X> fromDataState(list: List<X>, dataState: DataState<*>): CollectionState<X> {
            return CollectionState(
                data = list,
                localEnabled = dataState.localEnabled,
                localLoading = dataState.localLoading,
                remoteEnabled = dataState.remoteEnabled,
                remoteLoading = dataState.remoteLoading,
                error = dataState.error,
                refreshLoading = dataState.refreshLoading
            )
        }

        fun <T> subject(
            localEnabled: Boolean = false,
            remoteEnabled: Boolean = false,
            paginationEnabled: Boolean = false,
            data: List<T> = arrayListOf()
        ): BehaviorSubject<CollectionState<T>> {
            return BehaviorSubject.createDefault(
                CollectionState(
                    data,
                    localEnabled = localEnabled,
                    remoteEnabled = remoteEnabled || paginationEnabled,
                    paginationEnabled = paginationEnabled
                )
            )
        }

        fun <T> binding(
            localEnabled: Boolean = false,
            remoteEnabled: Boolean = false,
            paginationEnabled: Boolean = false,
            data: List<T> = arrayListOf()
        ): ObservableField<CollectionState<T>> {
            return ObservableField(
                CollectionState(
                    data,
                    localEnabled = localEnabled,
                    remoteEnabled = remoteEnabled || paginationEnabled,
                    paginationEnabled = paginationEnabled
                )
            )
        }
    }
}