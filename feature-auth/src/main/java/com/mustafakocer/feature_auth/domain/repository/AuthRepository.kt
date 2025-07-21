package com.mustafakocer.feature_auth.domain.repository

import com.mustafakocer.core_common.util.Resource
import kotlinx.coroutines.flow.Flow
import com.mustafakocer.core_common.provider.SessionProvider

interface AuthRepository : SessionProvider {
    fun createRequestToken(): Flow<Resource<String>>
    fun createSession(requestToken: String): Flow<Resource<String>>

    suspend fun clearSession()
}