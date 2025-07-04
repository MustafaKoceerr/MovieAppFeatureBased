package com.mustafakocer.database_contracts.entities

/**
 * Pageable entity contract
 * For entities that support pagination
 */
interface PageableEntityContract : BaseEntityContract {
    val page: Int
    val position: Int
    val category: String
}