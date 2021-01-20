package com.tymate.core

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class Mapper<L, R> {

    fun safeTransform(item: R?): L? {
        if (item == null) {
            return null
        } else {
            return transform(item)
        }
    }

    fun safeTransform(items: List<R>?): List<L>? = items?.map { transform(it) }

    fun safeInverseTransform(item: L?): R? {
        if (item == null) {
            return null
        } else {
            return inverseTransform(item)
        }
    }

    fun safeInverseTransform(items: List<L>?): List<R>? = items?.map { inverseTransform(it) }

    abstract fun transform(item: R): L

    fun transform(items: List<R>): List<L> = items.map { transform(it) }

    abstract fun inverseTransform(item: L): R

    fun inverseTransform(items: List<L>): List<R> = items.map { inverseTransform(it) }

}