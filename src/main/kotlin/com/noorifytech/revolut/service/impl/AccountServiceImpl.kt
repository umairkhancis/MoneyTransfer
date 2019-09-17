package com.noorifytech.revolut.service.impl

import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.dto.HttpStatusCode
import com.noorifytech.revolut.dto.Response
import com.noorifytech.revolut.repository.AccountRepository
import com.noorifytech.revolut.service.AccountService

class AccountServiceImpl(private val accountRepository: AccountRepository) : AccountService {
    override suspend fun getAllAccounts(): Response<List<AccountDto>> {
        val accounts = accountRepository.get()
        return Response(accounts, HttpStatusCode.OK.value, HttpStatusCode.OK.description)
    }

    override suspend fun getAccount(id: Int): Response<AccountDto> {
        val account = accountRepository.get(id)
        return if (account == null) Response(null, HttpStatusCode.NotFound.value, "Account Not Found")
        else Response(account, HttpStatusCode.OK.value, HttpStatusCode.OK.description)
    }
}
