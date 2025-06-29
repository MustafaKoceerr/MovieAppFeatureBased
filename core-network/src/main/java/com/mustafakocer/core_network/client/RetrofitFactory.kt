package com.mustafakocer.core_network.client

import retrofit2.Retrofit

interface RetrofitFactory {
    fun createRetrofit(): Retrofit
    fun <T> createApiService(serviceClass: Class<T>): T
}