package com.noorifytech.revolut.repository.impl

import com.noorifytech.revolut.dao.AccountTransactionDao
import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.repository.AccountTransactionRepository

class AccountTransactionRepositoryImpl(private val dao: AccountTransactionDao) : AccountTransactionRepository {
    override suspend fun get(): List<AccountTransactionDto> = dao.get()

    override suspend fun get(id: Int): AccountTransactionDto? = dao.get(id)

    override suspend fun create(data: AccountTransactionDto): AccountTransactionDto? = dao.create(data)
}