package com.tymate.core.state

import androidx.databinding.ObservableField
import com.tymate.core.state.base.State
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
data class DataState<T>(
    override val data: T? = null,
    override val localEnabled: Boolean = false,
    override val localLoading: Boolean = localEnabled,
    override val remoteEnabled: Boolean = false,
    override val remoteLoading: Boolean = remoteEnabled,
    override val refreshLoading: Boolean = false,
    override val error: Throwable? = null,
    override val fromPartial: State.Partial<T>? = null
) : State<T> {

    var payload = UUID.randomUUID()

    override fun isDataEmpty(data: T?): Boolean {
        return when (data) {
            null -> true
            else -> false
        }
    }

    fun <X> copy(newData: X): DataState<X> {
        return DataState(
            data = newData,
            localEnabled = localEnabled,
            localLoading = localLoading,
            remoteEnabled = remoteEnabled,
            remoteLoading = remoteLoading,
            refreshLoading = refreshLoading,
            error = error
        )
    }

    fun <X> copy(newData: List<X>): CollectionState<X> {
        return CollectionState(
            data = newData,
            localEnabled = localEnabled,
            localLoading = localLoading,
            remoteEnabled = remoteEnabled,
            remoteLoading = remoteLoading,
            error = error,
            refreshLoading = refreshLoading,
            paginationEnabled = false
        )
    }

    override fun reduce(partialState: State.Partial<T>): DataState<T> {
        return when (partialState) {
            is State.Partial.LocalLoad -> this.copy(localLoading = true, fromPartial = partialState)
            is State.Partial.RemoteLoad -> this.copy(remoteLoading = true, fromPartial = partialState)
            is State.Partial.PullLoad -> this.copy(data = null, refreshLoading = true, fromPartial = partialState)


            is State.Partial.LocalSuccess -> {
                val partialData = partialState.data
                if (!isDataEmpty(partialData) || !remoteEnabled) {
                    this.copy(data = partialData, localLoading = false, fromPartial = partialState)
                } else if (!remoteLoading && isDataEmpty(partialData)) {
                    this.copy(localLoading = false, fromPartial = partialState)
                } else {
                    this.copy(fromPartial = partialState)
                }
            }
            is State.Partial.RemoteSuccess -> {
                val partialData = partialState.data
                this.copy(
                    remoteLoading = false,
                    localLoading = localLoading && !isDataEmpty(partialData),
                    refreshLoading = false,
                    data = if (localEnabled) data else partialData,
                    fromPartial = partialState
                )
            }


            is State.Partial.LocalError -> this.copy(
                error = partialState.error,
                localLoading = false,
                remoteLoading = false,
                data = null,
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
                error = null,
                data = null,
                fromPartial = partialState
            )
            is State.Partial.Empty -> this.copy(
                remoteLoading = false,
                localLoading = false,
                refreshLoading = false,
                error = null,
                data = null,
                fromPartial = partialState
            )
            else -> this.copy()
        }
    }

    companion object {

        fun <T> initialState(): DataState<T> {
            return DataState()
        }

        fun <T> subject(): BehaviorSubject<DataState<T>> {
            return BehaviorSubject.createDefault<DataState<T>>(initialState())
        }

        fun <T> subject(initialState: DataState<T>): BehaviorSubject<DataState<T>> {
            return BehaviorSubject.createDefault<DataState<T>>(initialState)
        }

        fun <T> subject(
            localEnabled: Boolean = false,
            remoteEnabled: Boolean = false,
            data: T? = null
        ): BehaviorSubject<DataState<T>> {
            return subject(
                DataState(
                    data,
                    localEnabled = localEnabled,
                    remoteEnabled = remoteEnabled
                )
            )
        }

        fun <T> binding(
            localEnabled: Boolean = false,
            remoteEnabled: Boolean = false,
            data: T? = null
        ): ObservableField<DataState<T>> {
            return ObservableField(
                DataState(
                    data,
                    localEnabled = localEnabled,
                    remoteEnabled = remoteEnabled
                )
            )
        }
    }
}