package com.mustafakocer.database_contracts.entities

/**
 * Base entity contract for all database entities
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Interface segregation - entities only expose what they need
 * ✅ Testable contracts - easy to mock for testing
 * ✅ Decoupled features - features don't know about concrete implementations
 * ✅ Clear responsibilities - each interface has specific purpose
 */

interface BaseEntityContract {
    val id: Int
}