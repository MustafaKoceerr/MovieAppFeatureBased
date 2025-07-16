package com.mustafakocer.data_common.preferences.provider

/**
 * Her api isteğinde veriyi DataStore'dan okumak verimsizdi.
 * Network config'in lifecycle scope'unu Singleton'dan ActivityScoped'a çekersek, her dil değiştiğinde activity recrate olduğu için bu sorunu çözebiliriz ama bu seferde,
 * Sadece dili değiştirmek için birçok network bağımlılığı (retrofit, okhttpclient) vs yeniden oluşturulur. Bu da verimsizdir.
 *
 * Üçüncü ve best case yol olarak: LanguageProvider yardımcı class'ıyla bir kere language'yi okuyacağız ve Bu aracı class yardımıyla bu işi çözeceğiz.
 * // todo yapıyı anladıktan sonra daha iyi bir açıklama yaz.
 */
import com.mustafakocer.data_common.preferences.repository.LanguageRepository
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