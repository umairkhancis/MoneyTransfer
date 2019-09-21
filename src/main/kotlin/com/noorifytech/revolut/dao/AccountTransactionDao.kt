package com.noorifytech.revolut.dao

import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.exception.TransactionFailedException

interface AccountTransactionDao : BaseDao<AccountTransactionDto> {
    @Throws(TransactionFailedException::class)
    suspend fun create(data: AccountTransactionDto): AccountTransactionDto?
}