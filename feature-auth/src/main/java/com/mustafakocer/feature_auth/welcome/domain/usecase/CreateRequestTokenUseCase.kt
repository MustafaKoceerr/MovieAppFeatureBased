package com.mustafakocer.feature_auth.welcome.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateRequestTokenUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(): Flow<Resource<String>> {
        return loginRepository.createRequestToken()
    }
}