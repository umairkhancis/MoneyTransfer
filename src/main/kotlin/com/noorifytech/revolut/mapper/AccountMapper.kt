package com.noorifytech.revolut.mapper

import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.entity.Accounts
import org.jetbrains.exposed.sql.ResultRow

object AccountMapper {
    fun toAccountDto(row: ResultRow): AccountDto =
            AccountDto(id = row[Accounts.id],
                    name = row[Accounts.name],
                    userId = row[Accounts.userId],
                    balance = row[Accounts.balance],
                    dateUpdated = row[Accounts.dateUpdated]
            )
}