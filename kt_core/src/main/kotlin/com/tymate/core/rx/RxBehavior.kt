package com.tymate.core.rx

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class RxBehavior<T> private constructor(private val duration: Long = 0, private val timeUnit: TimeUnit? = null) {

    private var subject: BehaviorSubject<T>
    private var hasTimeValidity = false
    private var expirationDate: Long = 0
    private var tag: String? = null

    init {
        if (timeUnit == null) {
            hasTimeValidity = false
        } else {
            hasTimeValidity = true
            expirationDate = timeUnit.toMillis(duration) + System.currentTimeMillis()
        }
        subject = BehaviorSubject.create()
    }

    fun isValid(tag: String? = null): Boolean {
        if (subject.hasThrowable()) {
            return false
        }
        return if (hasTimeValidity) {
            isTagValid(tag) && !isExpired()
        } else {
            isTagValid(tag)
        }
    }

    fun isExpired(): Boolean {
        return expirationDate < System.currentTimeMillis()
    }

    fun isTagValid(tag: String?): Boolean {
        return tag == null || tag == this.tag
    }

    fun subject(): BehaviorSubject<T> {
        return subject
    }

    fun asObservable(): Observable<T> {
        return subject.take(1)
    }

    fun asSingle(): Single<T> {
        return subject.take(1).singleOrError()
    }

    fun invalidate() {
        hasTimeValidity = true
        expirationDate = 0
    }

    @JvmOverloads
    fun onNext(t: T, tag: String? = null) {
        this.tag = tag
        subject.onNext(t)
    }

    fun onError(throwable: Throwable) {
        if (subject.hasObservers()) {
            subject.onError(throwable)
        }
    }

    companion object {

        fun <T> create(): RxBehavior<T> {
            return RxBehavior()
        }

        fun <T> createWithTime(duration: Long, timeUnit: TimeUnit): RxBehavior<T> {
            return RxBehavior(duration, timeUnit)
        }

    }

//    private var subject: BehaviorSubject<T>? = null
//    private var hastimeValidity = false
//    private var expirationDate: Long = 0
//
//    val isValid: Boolean
//        get() {
//            if (subject == null || subject?.hasThrowable() == true) {
//                return false
//            }
//            return if (hastimeValidity) expirationDate > System.currentTimeMillis() else true
//        }
//
//    private fun computeExpiration() {
//        if (timeUnit == null) {
//            hastimeValidity = false
//        } else {
//            hastimeValidity = true
//            expirationDate = timeUnit.toMillis(duration) + System.currentTimeMillis()
//        }
//    }
//
//    fun subject(): BehaviorSubject<T> {
//        var subject = subject
//        if (isValid && subject != null) {
//            return subject
//        } else {
//            computeExpiration()
//            subject= BehaviorSubject.create()
//            this.subject = subject
//            return subject
//        }
//    }
}

class Cache<T>(val duration: Long, val timeUnit: TimeUnit) {

    private var cache = RxBehavior.createWithTime<T>(0, TimeUnit.MINUTES)

    fun load(request: Single<T>, tag: String? = null): Single<T> {
        if (cache.isValid(tag)) {
            return cache.asSingle()
        }
        cache = RxBehavior.createWithTime(duration, timeUnit)
        return request.bindTo(cache, tag)
    }

    fun load(request: Observable<T>, tag: String? = null): Observable<T> {
        if (cache.isValid(tag)) {
            return cache.asObservable()
        }
        cache = RxBehavior.createWithTime(duration, timeUnit)
        return request.bindTo(cache, tag)
    }

    val value: T?
        get() = cache.subject().value

    fun isValid(tag: String? = null): Boolean {
        return cache.isValid(tag)
    }
}

fun <T> Single<T>.bindTo(rxBehavior: RxBehavior<T>, tag: String? = null): Single<T> {
    return this.doOnSuccess { rxBehavior.onNext(it, tag) }.doOnError { rxBehavior.onError(it) }
}

fun <T> Single<T>.bindTo(cache: Cache<T>, tag: String? = null): Single<T> {
    return cache.load(this, tag)
}


fun <T> Observable<T>.bindTo(rxBehavior: RxBehavior<T>, tag: String? = null): Observable<T> {
    return this.doOnNext { rxBehavior.onNext(it, tag) }.doOnError { rxBehavior.onError(it) }
}
fun <T> Observable<T>.bindTo(cache: Cache<T>, tag: String? = null): Observable<T> {
    return cache.load(this, tag)
}
