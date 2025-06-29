package com.mustafakocer.core_network.client

import okhttp3.OkHttpClient

interface HttpClientFactory {
    fun createClient(): OkHttpClient
}