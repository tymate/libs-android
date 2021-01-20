package com.tymate.core.error

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager


open class NetworkManager constructor(private val application: Application) : BroadcastReceiver() {

    private val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    private val listeners = HashMap<String, Listener>()
    val isNetworkAvailable: Boolean
        get() {
            return isNetworkAvailable(application)
//            val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
////            NetworkRequest.Builder()
////                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
////                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
////                    .build()
////
////
////            cm.getNetworkCapabilities(cm.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//
////            ConnectivityManager.NetworkCallback.onCapabilitiesChanged(Network, NetworkCapabilities) or
////            cm.registerNetworkCallback()
////            NetworkCapabilities().hasCapability(NetworkCapabilities.TRANSPORT_WIFI)
//            val activeNetwork = cm.activeNetworkInfo ?: return false
//            return when (activeNetwork.type) {
//                ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE -> true
//                else -> false
//            }
        }

    fun register() {
        application.registerReceiver(this, intentFilter)
    }

    fun unregister() {
        application.unregisterReceiver(this)
    }

    fun add(tag: String, listener: Listener) {
        listeners[tag] = listener
    }

    fun remove(tag: String) {
        listeners.remove(tag)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!isNetworkAvailable) {
            return
        }
        if (isInitialStickyBroadcast) {
            return
        }
        for (listener in listeners.values) {
            listener.onNetworkAvailable()
        }
    }

    interface Listener {
        fun onNetworkAvailable()
    }

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo ?: return false
            return when (activeNetwork.type) {
                ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE -> true
                else -> false
            }
        }
    }
}
