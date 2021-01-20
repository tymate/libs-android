package com.tymate.core.state.base

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
interface ViewState<PS : PartialState> {

    fun reduce(partialState: PS): ViewState<PS>
}

interface InitialState<S> {
    val initialState: S
}