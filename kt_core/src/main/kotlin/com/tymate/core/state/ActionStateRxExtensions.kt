package com.tymate.core.state

import androidx.annotation.CheckResult
import androidx.databinding.ObservableField
import com.tymate.core.state.base.LoadEvent
import com.tymate.core.state.base.ResultDisposable
import com.tymate.core.state.base.load
import io.reactivex.Observable
import io.reactivex.Single

@CheckResult
fun <T> Observable<T>.execute(
    field: ObservableField<ActionState>,
    onPartial: ((ActionState.Partial) -> Unit)? = null
): ResultDisposable<T> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> ActionState.Partial.Execute()
            is LoadEvent.Success -> ActionState.Partial.Success(
                loadEvent.data
            )
            is LoadEvent.Error -> ActionState.Partial.Failure(
                loadEvent.error
            )
        }
    }, onPartial = onPartial)
}

@CheckResult
fun <T> Single<T>.execute(field: ObservableField<ActionState>): ResultDisposable<T> {
    return load(field, converter = { loadEvent ->
        when (loadEvent) {
            is LoadEvent.Load -> ActionState.Partial.Execute()
            is LoadEvent.Success -> ActionState.Partial.Success(loadEvent.data)
            is LoadEvent.Error -> ActionState.Partial.Failure(loadEvent.error)
        }
    })

}