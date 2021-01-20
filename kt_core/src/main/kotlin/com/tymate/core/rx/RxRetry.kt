package com.tymate.core.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

class RxRetry(
        private val maxRetries: Int,
        private val retryDelayMillis: Long,
        private val canRetry: ((Throwable) -> Boolean)? = null
) : Function<Observable<out Throwable>, Observable<*>> {

    private var retryCount: Int = 0

    init {
        this.retryCount = 0
    }

    override fun apply(t: Observable<out Throwable>): Observable<*> {
        return t.flatMap { throwable ->
            if (++retryCount < maxRetries && (canRetry == null || canRetry.invoke(throwable))) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
            } else Observable.error(throwable)
        }
    }
}


class RetrySingle(
    private val maxRetries: Int,
    private val retryDelayMillis: Long,
    private val canRetry: ((Throwable) -> Boolean)? = null
) : Function<Flowable<out Throwable>, Publisher<*>> {

    private var retryCount: Int = 0

    init {
        this.retryCount = 0
    }

    override fun apply(t: Flowable<out Throwable>): Publisher<*> {
        return t.flatMapSingle { throwable ->
            if (++retryCount < maxRetries && (canRetry == null || canRetry.invoke(throwable))) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                Single.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
            } else Single.error(throwable)
        }
    }
}
