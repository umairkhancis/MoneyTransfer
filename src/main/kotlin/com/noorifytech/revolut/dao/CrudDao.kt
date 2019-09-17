package com.noorifytech.revolut.dao

interface CrudDao<T> {
    suspend fun get(): List<T>
    suspend fun get(id: Int): T?
    suspend fun update(data: T): T?
    suspend fun create(data: T): T?
    suspend fun delete(id: Int): Boolean
}