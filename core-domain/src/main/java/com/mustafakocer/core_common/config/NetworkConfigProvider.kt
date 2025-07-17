package com.mustafakocer.core_common.config

interface NetworkConfigProvider {
    val baseUrl: String
    val apiKey: String
    val userAgent: String
    val isDebug: Boolean
    val enableLogging: Boolean
}
// core common modülünde interface olarak durmalı .
