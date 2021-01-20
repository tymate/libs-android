package com.tymate.core.adapter

import com.tymate.core.Equatable

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class SkeletonObject constructor(val tag : String = DEFAULT_TAG) : Equatable {

    override fun areItemsTheSame(other: Any?): Boolean {
        return other is SkeletonObject
    }

    override fun areContentsTheSame(other: Any?): Boolean {
        return true
    }

    companion object {

        internal val DEFAULT_TAG = "default"
    }
}
