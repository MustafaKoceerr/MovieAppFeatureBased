package com.mustafakocer.core_network.connectivity
// # Connection state management
/**
 * TEACHING MOMENT: Network Connection State
 *
 * Bu data class network durumunu temsil eder
 * ✅ Simple ve immutable
 * ✅ Type-safe network type
 * ✅ Easy to use in UIz
 */

data class ConnectionState(
    val isConnected: Boolean,
    val networkType: NetworkType
)

enum class NetworkType {
    WIFI, CELLULAR, ETHERNET, UNKNOWN, NONE
}