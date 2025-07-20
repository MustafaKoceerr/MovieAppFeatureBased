package com.mustafakocer.core_network.connectivity

/**
 * Ağ bağlantısının durumunu daha detaylı modelleyen sealed interface.
 */
sealed interface ConnectivityStatus {
    data object Connected : ConnectivityStatus

    data object Disconnected : ConnectivityStatus

    /**
     * Bağlantının var olduğunu ancak zayıf, yavaş veya kısıtlı olabileceğini belirtir.
     * Gelecekte, kullanıcının mobil veri paketinden harcayan bir bağlantı olup olmadığını
     * kontrol etmek için kullanılabilir.
     * @param metered Mobil veri gibi ücretli bir bağlantı mı?
     */
    data class Degraded(val metered: Boolean) : ConnectivityStatus
}