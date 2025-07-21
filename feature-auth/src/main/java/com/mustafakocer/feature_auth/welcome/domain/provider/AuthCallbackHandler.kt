package com.mustafakocer.feature_auth.welcome.domain.provider

import kotlinx.coroutines.flow.Flow

/**
 * Kimlik doğrulama geri dönüşlerini (callbacks) dinlemek için
 * 'domain' katmanında tanımlanmış temiz bir arayüz.
 *
 * Bu, 'data' katmanındaki implementasyon detaylarını gizler.
 */
interface AuthCallbackProvider {
    val tokenFlow: Flow<String>
}