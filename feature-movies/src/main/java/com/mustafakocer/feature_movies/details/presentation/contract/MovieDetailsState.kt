package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_ui.component.error.ErrorInfo
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails

/**
 * BEST PRACTICE: Hybrid State Pattern (Sealed Interface + Data Class)
 *
 * Bu yaklaşım:
 * ✅ Ana durumları (Loading, Success, Error) net bir şekilde ayırır
 * ✅ Derleyici garantisiyle imkansız durumları önler
 * ✅ Veri koruma (data preservation) mantığını zarifçe çözer
 * ✅ State sınıfını basit ve odaklı tutar
 * ✅ KISS prensibine uygun
 */
sealed interface MovieDetailsUiState {

    /**
     * Ekranın ilk kez yüklendiği veya tam ekran hatadan sonra yeniden denendiği durum.
     * Sadece tam ekran bir yükleme göstergesi gösterilir.
     */
    object InitialLoading : MovieDetailsUiState

    /**
     * Verinin başarıyla yüklendiği ve gösterildiği ana durum.
     * Bu durum, arka planda olabilecek ikincil olayları da (refresh, snackbar mesajı) yönetir.
     */
    data class Success(
        val movieDetails: MovieDetails,
        val isRefreshing: Boolean = false,      // Arka planda yenileme var mı? (Pull-to-refresh vb.)
        val isSharing: Boolean = false,         // Share işlemi devam ediyor mu?
    ) : MovieDetailsUiState {
        /**
         * Check if sharing is in progress
         */
        fun isSharingProgress(): Boolean = isSharing
    }

    data class Error(
        val exception: AppException,
        val errorInfo: ErrorInfo,
    ) : MovieDetailsUiState

    /**
     * Hiçbir veri gösterilemediğinde ortaya çıkan ve tüm ekranı kaplayan hata durumu.
     */
    data class FullScreenError(
        val message: String,
        val canRetry: Boolean = true
    ) : MovieDetailsUiState
}


/**
 * Snackbar veya Dialog gibi geçici, eyleme dönük olmayan mesajları temsil eder.
 * ID, aynı mesajın yapılandırma değişikliklerinde tekrar gösterilmesini önler.
 */
data class UserMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String
)