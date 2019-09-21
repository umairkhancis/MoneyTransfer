package com.noorifytech.revolut.dao.impl.db

import com.noorifytech.revolut.exception.TransactionFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

abstract class Database {
    abstract fun init()

    suspend fun <T> query(
            block: (transaction: Transaction) -> T): T =
            withContext(Dispatchers.IO) {
                transaction { block(this) }
            }

    @Throws(TransactionFailedException::class)
    suspend fun <T> executeTransaction(
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

}