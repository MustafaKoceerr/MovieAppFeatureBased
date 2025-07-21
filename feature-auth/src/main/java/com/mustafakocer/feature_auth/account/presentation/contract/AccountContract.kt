package com.mustafakocer.feature_auth.account.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState

data class AccountUiState(
    val isLoggedIn: Boolean = false,
    // Gelecekte vuraya kullanıcı adı, avatar gibi bilgiler eklenebilir.
    // val username: String? = null

) : BaseUiState {
    override val isLoading: Boolean = false
    override val isRefreshing: Boolean = false
    override val error: AppException? = null
}

sealed interface AccountEvent : BaseUiEvent {
    object LogoutClicked : AccountEvent
    object LoginClicked : AccountEvent
    object BackClicked : AccountEvent
}

sealed interface AccountEffect : BaseUiEffect {
    object NavigateToWelcome : AccountEffect
    object NavigateBack : AccountEffect
}