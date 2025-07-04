package com.mustafakocer.database_contracts

import com.mustafakocer.database_contracts.dao.BaseDaoContract
import com.mustafakocer.database_contracts.dao.CacheAwareDaoContract
import com.mustafakocer.database_contracts.dao.MovieDaoContract

/**
 * Public API for database-contracts module
 * Kolay kullanım için type alias'lar ve import'lar
 */


// DAO Contracts - Re-export for easy consumption
typealias BaseDao<T> = com.mustafakocer.database_contracts.dao.BaseDaoContract<T>
typealias CacheAwareDao<T> = com.mustafakocer.database_contracts.dao.CacheAwareDaoContract<T>
typealias MovieDao<T> = com.mustafakocer.database_contracts.dao.MovieDaoContract<T>

// Database Contract - Re-export for easy consumption
typealias AppDatabase = com.mustafakocer.database_contracts.database.AppDatabaseContract

/**
 * USAGE EXAMPLES:
 *
 * // Feature module'da:
 * import com.mustafakocer.database_contracts.*
 *
 * interface MovieListDao : BaseDao<MovieListEntity>, MovieDao<MovieListEntity> {
 *     // DAO implementation
 * }
 *
 * // App module'da:
 * abstract class AppDatabase : AppDatabase() {
 *     // Database implementation
 * }
 */