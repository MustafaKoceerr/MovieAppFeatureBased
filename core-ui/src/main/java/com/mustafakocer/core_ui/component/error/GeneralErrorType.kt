package com.mustafakocer.core_ui.component.error

/**
 * Global infrastructure errors that have the same meaning
 * regardless of the context/feature where they occur.
 *
 * These errors represent technical/infrastructure problems,
 * not business domain issues.
 */
enum class GeneralErrorType {
    /**
     * No internet connection - same message everywhere
     * "Please check your internet connection"
     */
    NO_INTERNET,

    /**
     * Server timeout - same technical issue everywhere
     * "Request timed out, please try again"
     */
    SERVER_TIMEOUT,

    /**
     * Server unreachable - same infrastructure problem
     * "Cannot reach server, please try again later"
     */
    SERVER_UNREACHABLE,

    /**
     * 5xx Server errors - same server-side issue
     * "Our servers are experiencing issues"
     */
    SERVER_ERROR,

    /**
     * 429 Rate limiting - same throttling everywhere
     * "Too many requests, please wait a moment"
     */
    RATE_LIMITED,

    /**
     * Unknown/unexpected errors - generic fallback
     * "Something unexpected happened"
     */
    UNKNOWN
}

/**
 * CONTEXTUAL ERRORS (handled in ViewModels):
 *
 * - 404 Not Found → Context-specific messages
 * - 400 Bad Request → Different validation messages
 * - 401 Unauthorized → Different auth contexts
 * - 403 Forbidden → Different permission contexts
 * - Data parsing errors → Feature-specific messages
 * - Empty responses → Different "no data" messages
 */