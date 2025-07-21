package com.mustafakocer.feature_auth.account.domain.repository

import com.mustafakocer.core_common.provider.SessionProvider

interface AccountRepository : SessionProvider {
    suspend fun clearSession()
}