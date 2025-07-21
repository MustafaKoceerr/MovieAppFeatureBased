package com.mustafakocer.feature_auth.account.domain.usecase

import com.mustafakocer.core_domain.provider.SessionProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val sessionProvider: SessionProvider,
) {
    operator fun invoke(): Flow<String?> {
        return sessionProvider.observeSessionId()
    }
}