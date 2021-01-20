package com.tymate.core.view_model

import android.view.View
import androidx.annotation.CheckResult
import com.tymate.core.util.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

abstract class MvViewModel<I, O> : ObservableViewModel() {

    private val ouputDispatcher = PublishSubject.create<O>()

    open fun perform(input: I) {

    }

    open fun dispatchOutput(output: O) {
        ouputDispatcher.onNext(output)
    }

    @CheckResult
    fun subscribe(onOutput: (O) -> Unit): Disposable {
        return ouputDispatcher.observeOn(AndroidSchedulers.mainThread()).subscribe(onOutput)
    }

    fun performClick(input: I) = View.OnClickListener {
        hideKeyboardForInput(input, it)
        perform(input)
    }

    open fun hideKeyboardForInput(input: I, view: View) {
        view.hideKeyboard()
    }
}