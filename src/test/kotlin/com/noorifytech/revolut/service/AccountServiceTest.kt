package com.noorifytech.revolut.service

import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.repository.AccountRepository
import com.noorifytech.revolut.service.impl.AccountServiceImpl
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

class AccountServiceTest {

    @Mock
    private lateinit var accountRepository: AccountRepository
    private lateinit var accountService: AccountService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        accountService = AccountServiceImpl(accountRepository)
    }

    @Test
    fun getAllAccounts() = runBlocking {
        // Arrange
        val expectedAccounts = ArrayList<AccountDto>()
        val account1 = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        val account2 = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        expectedAccounts.add(account1)
        expectedAccounts.add(account2)
        Mockito.`when`(accountRepository.get()).thenReturn(expectedAccounts)

        // Act
        val accounts = accountService.getAllAccounts().data

        // Assert
        assertThat(accounts).hasSize(2)
        assertThat(accounts?.get(0)).isEqualTo(account1)
        assertThat(accounts?.get(1)).isEqualTo(account2)

        Unit
    }

    @Test
    fun getAccount_whenAccountWithTheGivenIdExist_thenReturnsExpectedAccount() = runBlocking {
        // Arrange
        val accountId = 1
        val expectedAccount = AccountDto(accountId, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountRepository.get(1)).thenReturn(expectedAccount)

        // Act
        val account = accountService.getAccount(accountId).data

        // Assert
        assertThat(account).isEqualTo(expectedAccount)

        Unit
    }

    @Test
    fun getAccount_whenAccountWithTheGivenIdDoesNotExist_thenReturns404NotFoundResponse() = runBlocking {
        // Arrange
        val accountId = 0
        Mockito.`when`(accountRepository.get(0)).thenReturn(null)

        // Act
        val accountResponse = accountService.getAccount(accountId)

        // Assert
        assertThat(accountResponse.data).isEqualTo(null)
        assertThat(accountResponse.code).isEqualTo(404)
        assertThat(accountResponse.msg).isEqualTo("Account Not Found")

        Unit
    }
}