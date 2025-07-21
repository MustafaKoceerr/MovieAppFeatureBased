package com.mustafakocer.core_domain.provider

import kotlinx.coroutines.flow.Flow

/**
 * Uygulama genelinde oturum bilgisini sağlayan merkezi arayüz.
 * Bu arayüz "core" katmanında yaşar, böylece tüm feature modülleri
 * "feature-auth"' modülünü doğrudan tanımadan oturum durumuna erişebilir.
 * Bunu feature-auth'u hilt yardımıyla SessionProvider Impl'sini sağlayarak yapabiliriz.
 */

interface SessionProvider {
    /**
     * Mevcut session_id'yi veya kullanıcı giriş yapmamışsa null değerini
     * içeren bir akış döndürür.
     */
    fun observeSessionId(): Flow<String?>
}