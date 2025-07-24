package com.mustafakocer.feature_auth.account.domain.repository

import com.mustafakocer.core_domain.provider.SessionProvider

/**
 * Defines the contract for managing account-related data and actions.
 *
 * Architectural Note:
 * This repository interface defines the data operations required by the account feature's
 * domain layer. It extends `SessionProvider` to indicate that any implementation must also
 * be able to provide session information, centralizing related responsibilities. The data
 * layer will provide the concrete implementation of this contract.
 */
interface AccountRepository {
    /**
     * Clears the current user session and any related local data.
     */
    suspend fun logout()
}