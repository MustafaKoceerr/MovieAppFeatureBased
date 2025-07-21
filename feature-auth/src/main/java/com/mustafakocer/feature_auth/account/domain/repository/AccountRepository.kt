package com.mustafakocer.feature_auth.account.domain.repository

import com.mustafakocer.core_domain.provider.SessionProvider

interface AccountRepository : SessionProvider {
    suspend fun clearSession()
}