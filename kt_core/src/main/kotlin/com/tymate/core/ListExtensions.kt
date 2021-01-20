package com.tymate.core


fun <T, R> List<T>.safeMap(transform: (T) -> R?): List<R> {
    val result = arrayListOf<R>()
    var transformed: R?
    for (item in this) {
        transformed = transform(item)
        if (transformed != null) {
            result.add(transformed)
        }
    }
    return result
}

fun Collection<*>.isOutOfRange(position: Int): Boolean {
    return position < 0 || position >= size
}
