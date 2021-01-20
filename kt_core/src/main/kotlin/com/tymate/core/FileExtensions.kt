package com.tymate.core

import java.io.File

fun File.sizeInMb(): Int {
    return (length() / 1024 / 1024).toInt()
}