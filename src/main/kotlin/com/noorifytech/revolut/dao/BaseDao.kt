package com.noorifytech.revolut.dao

interface BaseDao<T> {
    suspend fun get(): List<T>
    suspend fun get(id: Int): T?
}