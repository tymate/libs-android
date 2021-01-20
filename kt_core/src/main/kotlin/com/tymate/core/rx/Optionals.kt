package com.tymate.core.rx

import io.reactivex.Observable

fun <T, O> Observable<T?>.filterNil(map: (T) -> O?): Observable<O> {
    return filter{ map(it) != null }.map { map(it)!! }
}
