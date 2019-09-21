package com.noorifytech.revolut.dao.impl

import com.noorifytech.revolut.dao.AccountDao
import com.noorifytech.revolut.dao.impl.db.Database
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.entity.Accounts
import com.noorifytech.revolut.mapper.AccountMapper
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class AccountDaoImpl(private val db: Database, private val accountMapper: AccountMapper) : AccountDao {

    override suspend fun get(): List<AccountDto> =
            db.query {
                Accounts.selectAll().map { accountMapper.toAccountDto(it) }
            }

    override suspend fun get(id: Int): AccountDto? = db.query {
        Accounts.select { (Accounts.id eq id) }
                .mapNotNull { accountMapper.toAccountDto(it) }
                .singleOrNull()
    }
}