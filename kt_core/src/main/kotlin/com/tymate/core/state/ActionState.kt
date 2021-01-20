package com.tymate.core.state

import androidx.databinding.ObservableField
import com.tymate.core.state.base.InitialState
import com.tymate.core.state.base.PartialState
import com.tymate.core.state.base.ViewState
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
data class ActionState(
    val data: Any?,
    val loading: Boolean = false,
    val error: Throwable? = null,
    val fromPartial: ActionState.Partial?
) :
    ViewState<ActionState.Partial> {

    override fun reduce(partialState: Partial): ActionState {
        return when (partialState) {
            is Partial.Execute -> this.copy(data = null, loading = true, error = null, fromPartial = partialState)
            is Partial.Success -> this.copy(data = partialState.response, loading = false, error = null, fromPartial = partialState)
            is Partial.Failure -> this.copy(loading = false, error = partialState.throwable, fromPartial = partialState)
            is Partial.Reset -> initialState.copy(fromPartial = partialState)
        }
    }


    sealed class Partial : PartialState {
        class Execute : Partial()
        class Success(val response: Any? = null) : Partial()
        class Failure(val throwable: Throwable) : Partial()
        class Reset : Partial()
    }

    companion object : InitialState<ActionState> {

        override val initialState: ActionState
            get() = ActionState(data = null, loading = false, error = null, fromPartial = null)

        val subject: BehaviorSubject<ActionState>
            get() = BehaviorSubject.createDefault<ActionState>(initialState)

        fun subject(initialState: ActionState): BehaviorSubject<ActionState> {
            return BehaviorSubject.createDefault<ActionState>(initialState)
        }

        val binding: ObservableField<ActionState>
            get() = ObservableField(initialState)

        fun binding(initialState: ActionState): ObservableField<ActionState> {
            return ObservableField(initialState)
        }
    }
}