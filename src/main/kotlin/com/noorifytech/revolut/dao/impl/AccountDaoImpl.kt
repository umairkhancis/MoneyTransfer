package com.noorifytech.revolut.dao.impl

import com.noorifytech.revolut.dao.AccountDao
import com.noorifytech.revolut.dao.impl.db.H2Database
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.entity.Accounts
import com.noorifytech.revolut.mapper.AccountMapper
import org.jetbrains.exposed.sql.*

class AccountDaoImpl(private val db: H2Database,
                     private val accountMapper: AccountMapper)
    : AccountDao {

    override suspend fun get(): List<AccountDto> =
            db.query {
                Accounts.selectAll().map { accountMapper.toAccountDto(it) }
            }

    override suspend fun get(id: Int): AccountDto? = db.query {
        Accounts.select { (Accounts.id eq id) }
                .mapNotNull { accountMapper.toAccountDto(it) }
                .singleOrNull()
    }

    override suspend fun create(data: AccountDto): AccountDto? {
        var key = 0
        db.query {
            key = Accounts.insert {
                it[name] = data.name
                it[userId] = data.userId
                it[balance] = data.balance
                it[dateUpdated] = System.currentTimeMillis()
            } get Accounts.id
        }

        return get(key)
    }

    override suspend fun update(data: AccountDto): AccountDto? {
        val id = data.id
        db.query {
            Accounts.update({ Accounts.id eq id }) {
                it[name] = data.name
                it[userId] = data.userId
                it[balance] = data.balance
                it[dateUpdated] = System.currentTimeMillis()
            }
        }

        return get(id)
    }

    override suspend fun delete(id: Int): Boolean = db.query {
        Accounts.deleteWhere { Accounts.id eq id } > 0
    }
}