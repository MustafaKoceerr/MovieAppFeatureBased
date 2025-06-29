package com.mustafakocer.core_common.environment

enum class Environment {
    DEVELOPMENT,
    STAGING,
    PRODUCTION;

    val isDebug: Boolean
        get() = this != PRODUCTION
}