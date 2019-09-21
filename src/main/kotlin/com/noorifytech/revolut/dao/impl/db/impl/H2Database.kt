package com.noorifytech.revolut.dao.impl.db.impl

import com.noorifytech.revolut.entity.AccountTransactions
import com.noorifytech.revolut.entity.Accounts
import com.noorifytech.revolut.entity.Users
import com.noorifytech.revolut.exception.TransactionFailedException
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.sql.Connection

object H2Database : com.noorifytech.revolut.dao.impl.db.Database {
    override fun init() {
        Database.connect(hikari())
        transaction {
            createTables()
            insertInitialData()
        }
    }

    override suspend fun <T> query(
            block: (transaction: Transaction) -> T): T =
            withContext(Dispatchers.IO) {
                transaction { block(this) }
            }

    @Throws(TransactionFailedException::class)
    override suspend fun <T> executeTransaction(
            block: (transaction: Transaction) -> T) =
            withContext(Dispatchers.IO) {
                /**
                 * TRANSACTION_SERIALIZABLE mode of transaction
                 * will make sure that transaction is ACID.
                 *
                 * Read more:
                 * https://github.com/JetBrains/Exposed/wiki/Transactions
                 */
                transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
                    try {
                        block(this)
                        this.commit()
                    } catch (ex: TransactionFailedException) {
                        this.rollback()
                        throw ex
                    } catch (e: Exception) {
                        this.rollback()
                        throw TransactionFailedException(e.message)
                    }
                }
            }


    private fun insertInitialData(): InsertStatement<Number> {
        insertUsersData()
        return insertAccountsData()
    }

    private fun createTables() {
        create(Users)
        create(Accounts)
        create(AccountTransactions)
    }

    private fun insertUsersData(): InsertStatement<Number> {
        Users.insert {
            it[name] = "Umair Ahmed Khan"
            it[email] = "umairkhan.cis@gmail.com"
            it[dateUpdated] = System.currentTimeMillis()
        }
        Users.insert {
            it[name] = "Aamir Ahmed Khan"
            it[email] = "aamirkhan@gmail.com"
            it[dateUpdated] = System.currentTimeMillis()
        }
        return Users.insert {
            it[name] = "Usman Ahmed Khan"
            it[email] = "aamirkhan@gmail.com"
            it[dateUpdated] = System.currentTimeMillis()
        }
    }

    private fun insertAccountsData(): InsertStatement<Number> {
        Accounts.insert {
            it[name] = "Umair Ahmed Khan"
            it[userId] = 1
            it[balance] = BigDecimal.valueOf(100)
            it[dateUpdated] = System.currentTimeMillis()
        }
        Accounts.insert {
            it[name] = "Aamir Ahmed Khan"
            it[userId] = 2
            it[balance] = BigDecimal.valueOf(100)
            it[dateUpdated] = System.currentTimeMillis()
        }
        return Accounts.insert {
            it[name] = "Usman Ahmed Khan"
            it[userId] = 3
            it[balance] = BigDecimal.valueOf(100)
            it[dateUpdated] = System.currentTimeMillis()
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}