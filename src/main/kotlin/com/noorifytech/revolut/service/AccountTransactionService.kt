package com.noorifytech.revolut.service

import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.dto.Response

interface AccountTransactionService {
    suspend fun getTransactions(): Response<List<AccountTransactionDto>>
    suspend fun getTransaction(id: Int): Response<AccountTransactionDto>
    suspend fun transfer(data: AccountTransactionDto): Response<AccountTransactionDto>
}
