package com.tymate.core.util

import kotlin.reflect.KProperty

//https://speakerdeck.com/ashdavies/leveraging-android-data-binding-with-kotlin?slide=13

interface ReadOnlyProperty<in R, out T> {
    operator fun getValue(thisRef: R, property: KProperty<*>): T
}

interface ReadWriteProperty<in R, T> {
    operator fun getValue(thisRef: R, property: KProperty<*>): T
    operator fun setValue(thisRef: R, property: KProperty<*>, value: T)
}