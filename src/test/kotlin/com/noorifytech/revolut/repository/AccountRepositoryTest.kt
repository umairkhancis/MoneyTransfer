package com.noorifytech.revolut.repository

import com.noorifytech.revolut.dao.AccountDao
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.repository.impl.AccountRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

class AccountRepositoryTest {

    @Mock
    private lateinit var accountDao: AccountDao
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        accountRepository = AccountRepositoryImpl(accountDao)
    }

    @Test
    fun get_whenAccountsExist_thenReturnsAccountsList() = runBlocking {
        // Arrange
        val expectedAccounts = ArrayList<AccountDto>()
        val account1 = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        val account2 = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        expectedAccounts.add(account1)
        expectedAccounts.add(account2)
        Mockito.`when`(accountDao.get()).thenReturn(expectedAccounts)

        // Act
        accountRepository.get()

        // Assert
        Assertions.assertThat(expectedAccounts).hasSize(2)
        Assertions.assertThat(expectedAccounts[0]).isEqualTo(account1)
        Assertions.assertThat(expectedAccounts[1]).isEqualTo(account2)

        Unit
    }

    @Test
    fun get_whenAccountsDoNotExist_thenReturnsEmptyAccountsList() = runBlocking {
        // Arrange
        val expectedAccounts = emptyList<AccountDto>()
        Mockito.`when`(accountDao.get()).thenReturn(expectedAccounts)

        // Act
        accountRepository.get()

        // Assert
        Assertions.assertThat(expectedAccounts).hasSize(0)

        Unit
    }


    @Test
    fun get_whenAccountWithTheGivenIdExist_thenReturnsExpectedAccount() = runBlocking {
        // Arrange
        val accountId = 1
        val expectedAccount = AccountDto(accountId, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountDao.get(accountId)).thenReturn(expectedAccount)

        // Act
        val account = accountRepository.get(accountId)

        // Assert
        Assertions.assertThat(account).isEqualTo(expectedAccount)

        Unit
    }

    @Test
    fun getAccount_whenAccountWithTheGivenIdDoesNotExist_thenReturnsNull() = runBlocking {
        // Arrange
        val nonExistentAccountId = 0
        Mockito.`when`(accountDao.get(nonExistentAccountId)).thenReturn(null)

        // Act
        val account = accountRepository.get(nonExistentAccountId)

        // Assert
        Assertions.assertThat(account).isEqualTo(null)

        Unit
    }
}