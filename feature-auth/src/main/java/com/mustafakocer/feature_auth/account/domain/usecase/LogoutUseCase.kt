package com.mustafakocer.feature_auth.account.domain.usecase

import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke() {
        accountRepository.logout()
    }
}