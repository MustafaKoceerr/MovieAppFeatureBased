package com.mustafakocer.core_network.util

private val SECRET_PATTERN = Regex(
    """(api_key=|session_id=|request_token=|"session_id"\s*:\s*"|"request_token"\s*:\s*")[^&"\s]+"""
)

/**
 * Masks secrets (API key, session id, request token) in a network log line, both in query
 * strings (`api_key=abc`) and in JSON bodies (`"session_id":"abc"`).
 */
fun String.redactSecrets(): String = replace(SECRET_PATTERN) { match ->
    "${match.groupValues[1]}***"
}
