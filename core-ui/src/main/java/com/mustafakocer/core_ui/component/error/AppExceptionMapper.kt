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
import com.mustafakocer.core_ui.R

/**
 * Maps a domain-layer [AppException] to a UI-specific [ErrorInfo] object.
 *
 * Architectural Note:
 * This function serves as the single, centralized translator between abstract domain errors
 * and concrete, user-facing information. It's a key responsibility of the UI layer to
 * perform this mapping.
 *
 * Why this is a `@Composable` function:
 * It requires access to the composable context to resolve localized string resources via
 * `stringResource()`. This ensures that the error messages displayed to the user respect
 * the device's current language settings. This approach keeps the domain layer completely
 * unaware of Android `Context` or resource IDs.
 *
 * @return An [ErrorInfo] object containing localized text and appropriate icons for display.
 */
@Composable
fun AppException.toErrorInfo(): ErrorInfo {
    return when (this) {
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