package com.mustafakocer.feature_auth.welcome.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * A use case dedicated to creating a request token to initiate the login flow.
 *
 * @param loginRepository The repository responsible for handling login operations.
 *
 * Architectural Note:
 * This use case encapsulates the single, specific business action of creating a request token.
 * This abstraction decouples the ViewModel from the data layer's implementation details and
 * clearly defines the business logic available. The `invoke` operator provides a clean,
 * idiomatic way to execute the action from the ViewModel.
 */
class CreateRequestTokenUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    /**
     * Executes the request token creation process.
     * @return A [Flow] emitting a [Resource] wrapping the request token string.
     */
    operator fun invoke(): Flow<Resource<String>> {
        return loginRepository.createRequestToken()
    }
}