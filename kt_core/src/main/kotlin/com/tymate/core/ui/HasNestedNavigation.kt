package com.tymate.core.ui

interface HasNestedNavigation {

    fun onBackPressed(): Boolean

    fun onNavigateUp(): Boolean
}