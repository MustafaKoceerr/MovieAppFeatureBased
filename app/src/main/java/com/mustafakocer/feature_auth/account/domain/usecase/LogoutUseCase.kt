package com.mustafakocer.feature_auth.account.domain.usecase

import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * A use case dedicated to handling the user logout process.
 *
 * @param accountRepository The repository responsible for handling account data operations.
 *
 * Architectural Note:
 * This use case encapsulates the single, specific action of logging out. This abstraction
 * decouples the ViewModel from the data layer's implementation details and clearly defines
 * the business logic available to the presentation layer. The `suspend invoke` operator
 * provides a clean, coroutine-friendly way to execute the action.
 */
class LogoutUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    /**
     * Executes the logout process.
     */
    suspend operator fun invoke() {
        accountRepository.logout()
    }
}