package com.mustafakocer.core_database.dao

/**
 * Extended base DAO with additional common operations
 *
 * RESPONSIBILITY: Provide advanced common operations
 */
interface ExtendedBaseDao<T, ID> : BaseDao<T> {

    /**
     * Find entity by ID
     * @param id Entity ID
     * @return Entity or null if not found
     */
    suspend fun findById(id: ID): T?

    /**
     * Find multiple entities by IDs
     * @param ids List of entity IDs
     * @return List of found entities
     */
    suspend fun findByIds(ids: List<ID>): List<T>

    /**
     * Check if entity exists by ID
     * @param id Entity ID
     * @return True if exists, false otherwise
     */
    suspend fun existsById(id: ID): Boolean

    /**
     * Delete entity by ID
     * @param id Entity ID
     * @return Number of rows deleted
     */
    suspend fun deleteById(id: ID): Int

    /**
     * Delete multiple entities by IDs
     * @param ids List of entity IDs
     * @return Number of rows deleted
     */
    suspend fun deleteByIds(ids: List<ID>): Int

    /**
     * Get total count of entities
     * @return Total count
     */
    suspend fun count(): Int

    /**
     * Delete all entities
     * @return Number of rows deleted
     */
    suspend fun deleteAll(): Int
}