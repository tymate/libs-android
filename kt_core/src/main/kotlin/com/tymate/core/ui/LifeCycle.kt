package com.tymate.core.ui

import android.os.Bundle

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
interface LifeCycle {

    fun onCreate(savedInstanceState: Bundle?)

    fun onStart()

    fun onStop()

    fun onSaveInstanceState(bundle: Bundle)

    fun onDestroy()

    public class Event private constructor(val event: String, val bundle: Bundle?) {

        companion object {
            const val CREATE = "CREATE"
            const val START = "START"
            const val STOP = "STOP"
            const val SAVE_STATE = "SAVE_STATE"
            const val DESTROY = "DESTROY"

            fun create(bundle: Bundle?): Event {
                return Event(CREATE, bundle)
            }

            fun start(): Event {
                return Event(START, null)
            }

            fun stop(): Event {
                return Event(STOP, null)
            }

            fun saveState(bundle: Bundle): Event {
                return Event(
                    SAVE_STATE,
                    bundle
                )
            }

            fun destroy(): Event {
                return Event(DESTROY, null)
            }
        }
    }
}
