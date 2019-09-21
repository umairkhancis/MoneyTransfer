package com.noorifytech.revolut.dao.impl

import com.noorifytech.revolut.dao.AccountTransactionDao
import com.noorifytech.revolut.dao.impl.db.Database
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.entity.AccountTransactions
import com.noorifytech.revolut.entity.Accounts
import com.noorifytech.revolut.exception.TransactionFailedException
import com.noorifytech.revolut.mapper.AccountMapper
import com.noorifytech.revolut.mapper.AccountTransactionMapper
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class AccountTransactionDaoImpl(private val db: Database,
                                private val accountMapper: AccountMapper,
                                private val accountTransactionMapper: AccountTransactionMapper)
    : AccountTransactionDao {

    override suspend fun get(): List<AccountTransactionDto> =
            db.query {
                AccountTransactions.selectAll().map { accountTransactionMapper.toAccountTransactionDto(it) }
            }

    override suspend fun get(id: Int): AccountTransactionDto? = db.query {
        AccountTransactions.select { (AccountTransactions.id eq id) }
                .mapNotNull { accountTransactionMapper.toAccountTransactionDto(it) }
                .singleOrNull()
    }

    override suspend fun create(data: AccountTransactionDto): AccountTransactionDto? {
        try {
            var key = 0
            db.executeTransaction {
                val srcAccount = getAccount(data.srcAccountId)
                val destAccount = getAccount(data.destAccountId)

                if (srcAccount.balance < data.amount) {
                    throw TransactionFailedException("Insufficient funds available in the source account!")
                }

                key = AccountTransactions.insert {
                    it[srcAccountId] = data.srcAccountId
                    it[destAccountId] = data.destAccountId
                    it[purpose] = data.purpose
                    it[amount] = data.amount
                    it[dateUpdated] = System.currentTimeMillis()
                } get AccountTransactions.id

                // Update src account balance => newBalance = currentBalance - transactionAmount
                Accounts.update({ Accounts.id eq data.srcAccountId }) {
                    it[balance] = srcAccount.balance.minus(data.amount)
                    it[dateUpdated] = System.currentTimeMillis()
                }

                // Update dest account balance => newBalance = currentBalance + transactionAmount
                Accounts.update({ Accounts.id eq data.destAccountId }) {
                    it[balance] = destAccount.balance.plus(data.amount)
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            return get(key)
        } catch (ex: TransactionFailedException) {
            throw ex
        }
    }

    private fun getAccount(accountId: Int): AccountDto {
        return Accounts.select { (Accounts.id eq accountId) }
                .mapNotNull { accountMapper.toAccountDto(it) }
                .single()
    }
}