package com.noorifytech.revolut.repository.impl

import com.noorifytech.revolut.dao.AccountDao
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.repository.AccountRepository

class AccountRepositoryImpl(private val dao: AccountDao) : AccountRepository {
    override suspend fun get(): List<AccountDto> = dao.get()

    override suspend fun get(id: Int): AccountDto? = dao.get(id)
}