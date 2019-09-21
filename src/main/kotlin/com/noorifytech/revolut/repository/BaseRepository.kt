package com.noorifytech.revolut.repository

interface BaseRepository<T> {
    suspend fun get(): List<T>
    suspend fun get(id: Int): T?
}