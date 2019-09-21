package com.noorifytech.revolut.dao.impl.db

import com.noorifytech.revolut.exception.TransactionFailedException
import org.jetbrains.exposed.sql.Transaction

interface Database {
    fun init()

    suspend fun <T> query(
            block: (transaction: Transaction) -> T): T

    @Throws(TransactionFailedException::class)
    suspend fun <T> executeTransaction(
            block: (transaction: Transaction) -> T)
}