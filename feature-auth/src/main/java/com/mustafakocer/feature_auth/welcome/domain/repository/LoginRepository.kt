package com.mustafakocer.feature_auth.welcome.domain.repository

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface LoginRepository : SessionProvider {
    fun createRequestToken(): Flow<Resource<String>>
    fun createSession(requestToken: String): Flow<Resource<String>>

}