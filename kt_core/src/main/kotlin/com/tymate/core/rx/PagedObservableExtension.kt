package com.tymate.core.rx

import com.tymate.core.Mapper
import com.tymate.core.Paged
import io.reactivex.Observable

fun <I, O> Observable<Paged<List<I>>>.transform(mapper: Mapper<O, I>): Observable<Paged<List<O>>> {
    return map {
        Paged(mapper.transform(it.data), it.totalPages)
    }
}