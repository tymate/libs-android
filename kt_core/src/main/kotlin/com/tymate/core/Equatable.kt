package com.tymate.core

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
interface Equatable {

    fun areItemsTheSame(other: Any?): Boolean

    fun areContentsTheSame(other: Any?): Boolean
}