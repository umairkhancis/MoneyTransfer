package com.noorifytech.revolut.repository

import com.noorifytech.revolut.dto.AccountTransactionDto

interface AccountTransactionRepository : BaseRepository<AccountTransactionDto> {
    suspend fun create(data: AccountTransactionDto): AccountTransactionDto?
}