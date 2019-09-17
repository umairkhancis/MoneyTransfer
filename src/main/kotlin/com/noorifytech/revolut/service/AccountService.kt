package com.noorifytech.revolut.service

import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.dto.Response

interface AccountService {
    suspend fun getAllAccounts(): Response<List<AccountDto>>
    suspend fun getAccount(id: Int): Response<AccountDto>
}
