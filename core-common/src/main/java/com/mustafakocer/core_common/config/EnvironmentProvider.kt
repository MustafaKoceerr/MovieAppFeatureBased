package com.mustafakocer.core_common.config

import com.mustafakocer.core_common.environment.Environment


interface EnvironmentProvider {
    val environment: Environment
    val enableMockData: Boolean
    val apiVersion: String
}