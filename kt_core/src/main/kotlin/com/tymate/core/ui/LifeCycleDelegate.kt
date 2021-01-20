package com.tymate.core.ui

import com.tymate.core.ui.LifeCycle.Event.Companion.CREATE
import com.tymate.core.ui.LifeCycle.Event.Companion.DESTROY
import com.tymate.core.ui.LifeCycle.Event.Companion.SAVE_STATE
import com.tymate.core.ui.LifeCycle.Event.Companion.START
import com.tymate.core.ui.LifeCycle.Event.Companion.STOP


/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class LifeCycleDelegate {

    var lifeCycles: MutableList<LifeCycle> = arrayListOf()

    fun fromArray(array: Array<LifeCycle>) {
        lifeCycles = array.toMutableList()
    }

    fun fromList(list: List<LifeCycle>) {
        lifeCycles = list.toMutableList()
    }

    fun dispatchEvent(event: LifeCycle.Event) {
        when (event.event) {
            CREATE -> lifeCycles.forEach { it.onCreate(event.bundle) }
            START -> lifeCycles.forEach { it.onStart() }
            STOP -> lifeCycles.forEach { it.onStop() }
            SAVE_STATE -> lifeCycles.forEach { it.onSaveInstanceState(event.bundle!!) }
            DESTROY -> lifeCycles.forEach { it.onDestroy() }
        }
    }
}