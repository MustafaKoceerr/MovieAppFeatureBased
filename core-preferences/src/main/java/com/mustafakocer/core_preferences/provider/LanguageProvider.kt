package com.mustafakocer.core_preferences.provider

import com.mustafakocer.core_preferences.repository.LanguageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kontrolü hilt'e vermek ve dependency sorunlarıyla uğraşmamak için @Singleton class kullandık.
 * Bunu yapan object'de var ama object'lerin parametresi olmadığı için init bloğunda bir şeyler yapmamız gerekiyordu. Uzun iş :) Hilt varken
 */
@Singleton
class LanguageProvider @Inject constructor(
    languageRepository: LanguageRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Dil parametresini hafızada tutacak değişken
    // 'volatile' keyword'ü, farklı thread'lerden erişimde görünürlüğü garanti eder.
    @Volatile
    private var currentLanguageParam: String = ""

    init {
        // Sınıf oluşturulduğunda, DataStore'dan ilk değeri senkron olarak al.
        // Bu, uygulamanın ilk isteğinin doğru dille gitmesini sağlar.
        scope.launch {
            languageRepository.languageFlow.collect { newLanguage ->
                // DataStore'dan ne zaman yeni bir dil değeri gelse,
                // bu blok tetiklenir ve hafızadaki değişkenimiz güncellenir.
                currentLanguageParam = newLanguage.apiParam
            }
        }
    }

    // Interceptor bu fonksiyonu çağıracak. Disk I/O'su yok, sadece hafızadan okuma var.
    fun getLanguageParam(): String {
        return currentLanguageParam
    }

}