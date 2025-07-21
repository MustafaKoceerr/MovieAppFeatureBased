package com.mustafakocer.feature_auth.account.presentation.viewmodel

import com.mustafakocer.core_android.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import  com.mustafakocer.feature_auth.account.presentation.contract.*

@HiltViewModel
class AccountViewModel @Inject constructor(
//    private val observeSessionUseCase: ObserveSessionUseCase,
//    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<AccountUiState, AccountEvent, AccountEffect>(
    initialState = AccountUiState()
) {
    override fun onEvent(event: AccountEvent) {
    }


}