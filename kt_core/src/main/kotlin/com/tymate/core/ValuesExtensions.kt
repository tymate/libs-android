package com.tymate.core


fun Float.inRange(min: Int? = null, max: Int? = null): Boolean {
    return this >= min ?: 0 && max == null || this < max!!
}

fun Int.inRange(min: Int? = null, max: Int? = null): Boolean {
    return this >= min ?: 0 && max == null || this < max!!
}