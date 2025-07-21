package com.mustafakocer.feature_auth.domain.usecase

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateRequestTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Resource<String>> {
        return authRepository.createRequestToken()
    }
}