package com.tymate.core

data class Paged<out T>(val data: T,
                        var totalPages: Int = 0,
                        var totalItems: Int = 0)