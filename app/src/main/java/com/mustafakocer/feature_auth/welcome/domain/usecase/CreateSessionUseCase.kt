package com.mustafakocer.feature_auth.welcome.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * A use case dedicated to exchanging a request token for a permanent session ID.
 *
 * @param loginRepository The repository responsible for handling login operations.
 *
 * Architectural Note:
 * This use case encapsulates the single, specific business action of finalizing a session.
 * This abstraction decouples the ViewModel from the data layer's implementation details and
 * clearly defines the business logic available. The `invoke` operator provides a clean,
 * idiomatic way to execute the action from the ViewModel.
 */
class CreateSessionUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    /**
     * Executes the session creation process.
     * @param requestToken The approved token received from the authentication callback.
     * @return A [Flow] emitting a [Resource] wrapping the final session ID string.
     */
    operator fun invoke(requestToken: String): Flow<Resource<String>> {
        return loginRepository.createSession(requestToken)
    }
}