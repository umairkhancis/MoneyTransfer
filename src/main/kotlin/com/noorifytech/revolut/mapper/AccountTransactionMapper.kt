package com.noorifytech.revolut.mapper

import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.entity.AccountTransactions
import org.jetbrains.exposed.sql.ResultRow

object AccountTransactionMapper {
    fun toAccountTransactionDto(row: ResultRow): AccountTransactionDto =
            AccountTransactionDto(id = row[AccountTransactions.id],
                    srcAccountId = row[AccountTransactions.srcAccountId],
                    destAccountId = row[AccountTransactions.destAccountId],
                    purpose = row[AccountTransactions.purpose],
                    amount = row[AccountTransactions.amount],
                    dateUpdated = row[AccountTransactions.dateUpdated]
            )
}