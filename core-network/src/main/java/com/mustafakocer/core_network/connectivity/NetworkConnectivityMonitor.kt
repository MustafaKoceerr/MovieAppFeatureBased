package com.mustafakocer.core_network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

// # Real-time connectivity
/**
 * TEACHING MOMENT: Real-time Network Monitoring
 *
 * ✅ Real-time connectivity updates
 * ✅ Network type detection (WiFi, Cellular)
 * ✅ Flow-based reactive approach
 * ✅ Automatic cleanup
 */

@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Observe network connectivity changes in real-time
     */
    fun observeConnectivity(): Flow<ConnectionState> = callbackFlow {

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val networkType = getNetworkType(network)
                trySend(ConnectionState(true, networkType))
            }

            override fun onLost(network: Network) {
                trySend(ConnectionState(false, NetworkType.NONE))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                val networkType = getNetworkType(network)
                val isConnected =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(ConnectionState(isConnected, networkType))
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Send current state immediately
        val currentState = getCurrentConnectionState()
        trySend(currentState)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()

    /**
     * Get current connection state synchronously
     */
    fun getCurrentConnectionState(): ConnectionState {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return if (networkCapabilities != null &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ) {
            val networkType = getNetworkType(activeNetwork)
            ConnectionState(true, networkType)
        } else {
            ConnectionState(false, NetworkType.NONE)
        }
    }

    private fun getNetworkType(network: Network?): NetworkType {
        if (network == null) return NetworkType.NONE

        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.CELLULAR
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }
    }
}