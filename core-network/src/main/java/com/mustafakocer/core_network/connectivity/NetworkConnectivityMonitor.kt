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

/**
 * Cihazın ağ bağlantısı durumunu gerçek zamanlı olarak izler ve bunu
 * zengin bir `ConnectivityStatus` akışı olarak yayınlar.
 *
 * Clean Architecture: Altyapı Katmanı (Infrastructure)
 * Sorumluluk: Android'in ConnectivityManager'ını soyutlayarak, uygulama geneli için
 * reaktif ve anlaşılır bir ağ durumu bilgisi sağlamak.
 */
@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Ağ bağlantısı durumundaki değişiklikleri gerçek zamanlı olarak Flow olarak yayınlar.
     * Akış, yalnızca durum değiştiğinde yeni bir değer yayar.
     */
    fun observe(): Flow<ConnectivityStatus> = callbackFlow {
        // Mevcut durumu hemen gönder
        trySend(getCurrentConnectivityStatus())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // onAvailable tek başına internet olduğunu garanti etmez, onCapabilitiesChanged beklenmeli.
            }

            override fun onLost(network: Network) {
                // Bir ağ kaybedildiğinde, genel durumu yeniden değerlendir.
                trySend(getCurrentConnectivityStatus())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                // Bir ağın yetenekleri değiştiğinde (örn. internete bağlandığında), durumu gönder.
                trySend(getStatusFromCapabilities(networkCapabilities))
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Flow sonlandığında callback'i kaldır.
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged() // Yalnızca durum gerçekten değiştiğinde yeni değer yay.

    /**
     * Mevcut ağ bağlantısı durumunu senkron olarak döndürür.
     * Bu, anlık bir kontrol gerektiğinde kullanışlıdır (örneğin RemoteMediator'da).
     */
    fun getCurrentConnectivityStatus(): ConnectivityStatus {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return getStatusFromCapabilities(capabilities)
    }

    /**
     * NetworkCapabilities nesnesini bizim anladığımız ConnectivityStatus'e çevirir.
     */
    private fun getStatusFromCapabilities(caps: NetworkCapabilities?): ConnectivityStatus {
        if (caps == null) {
            return ConnectivityStatus.Disconnected
        }

        // İnternet erişimi var mı ve bu erişim doğrulanmış mı?
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        if (hasInternet && isValidated) {
            // Bağlantı ücretli mi? (örn. mobil veri)
            val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            return if (isMetered) {
                ConnectivityStatus.Degraded(metered = true)
            } else {
                ConnectivityStatus.Connected
            }
        }

        return ConnectivityStatus.Disconnected
    }
}