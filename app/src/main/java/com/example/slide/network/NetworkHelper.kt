package com.example.photo_video_maker_with_song.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

object NetworkHelper {

    var isConnected: Boolean = false

    var iNetworkManager: INetworkManager? = null

    fun startNetworkCallback(context: Context) {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        cm.registerNetworkCallback(builder.build(), object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                iNetworkManager?.onNetworkChanged(isConnected)
            }

            override fun onLost(network: Network) {
                isConnected = false
                iNetworkManager?.onNetworkChanged(isConnected)
            }
        })
    }
}