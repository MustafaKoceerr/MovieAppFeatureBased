package com.mustafakocer.core_ui.component.error

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.R // :core-ui modülünün kendi R dosyası

/**
 * Bir AppException'ı, UI'da gösterebilecek zengin bir ErrorInfo nesnesine dönüştürür.
 * Bu, projedeki tüm hata->UI çevirme mantığının tek merkezidir.
 *
 * Bu fonksiyon @Composable'dır çünkü string kaynaklarına (`stringResource`) erişir.
 */

@Composable
fun AppException.toErrorInfo(): ErrorInfo {
    return when (this) {
        // Ağ hataları
        // --- Ağ Hataları ---
        is AppException.Network.NoInternet -> ErrorInfo(
            title = stringResource(id = R.string.error_title_no_internet),
            description = stringResource(id = R.string.error_desc_no_internet),
            icon = Icons.Default.WifiOff,
            emoji = "📡"
        )
        is AppException.Network.Timeout -> ErrorInfo(
            title = stringResource(id = R.string.error_title_timeout),
            description = stringResource(id = R.string.error_desc_timeout),
            icon = Icons.Default.CloudOff,
            emoji = "⏱️"
        )

        // --- API Hataları ---
        is AppException.Api.Unauthorized -> ErrorInfo(
            title = stringResource(id = R.string.error_title_unauthorized),
            description = stringResource(id = R.string.error_desc_unauthorized),
            icon = Icons.Default.Lock,
            emoji = "🔑"
        )
        is AppException.Api.NotFound -> ErrorInfo(
            title = stringResource(id = R.string.error_title_not_found),
            description = stringResource(id = R.string.error_desc_not_found),
            icon = Icons.Default.SearchOff,
            emoji = "🤷"
        )
        is AppException.Api.ServerError -> ErrorInfo(
            title = stringResource(id = R.string.error_title_server),
            description = stringResource(id = R.string.error_desc_server),
            icon = Icons.Default.Error,
            emoji = "🔧"
        )

        // --- Veri Hataları ve Bilinmeyen Hatalar ---
        is AppException.Data.Parse,
        is AppException.Data.EmptyResponse,
        is AppException.Unknown -> ErrorInfo(
            title = stringResource(id = R.string.error_title_unknown),
            description = stringResource(id = R.string.error_desc_unknown),
            icon = Icons.Default.Error,
            emoji = "😕"
        )
    }
}