package com.noorifytech.revolut.repository

import com.noorifytech.revolut.dao.AccountTransactionDao
import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.repository.impl.AccountTransactionRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

class AccountTransactionRepositoryTest {

    @Mock
    private lateinit var accountTransactionDao: AccountTransactionDao
    private lateinit var accountTransactionRepository: AccountTransactionRepository

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        accountTransactionRepository = AccountTransactionRepositoryImpl(accountTransactionDao)
    }

    @Test
    fun get_whenAccountsExist_thenReturnsAccountsList() = runBlocking {
        // Arrange
        val expectedTransactions = ArrayList<AccountTransactionDto>()
        val accountTransaction1 = AccountTransactionDto(1, "Salary", 1, 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        val accountTransaction2 = AccountTransactionDto(2, "Salary", 2, 3, BigDecimal.valueOf(100), System.currentTimeMillis())
        expectedTransactions.add(accountTransaction1)
        expectedTransactions.add(accountTransaction2)
        Mockito.`when`(accountTransactionDao.get()).thenReturn(expectedTransactions)

        // Act
        accountTransactionRepository.get()

        // Assert
        Assertions.assertThat(expectedTransactions).hasSize(2)
        Assertions.assertThat(expectedTransactions[0]).isEqualTo(accountTransaction1)
        Assertions.assertThat(expectedTransactions[1]).isEqualTo(accountTransaction2)

        Unit
    }

    @Test
    fun get_whenAccountsDoNotExist_thenReturnsEmptyAccountsList() = runBlocking {
        // Arrange
        val expectedTransactions = emptyList<AccountTransactionDto>()
        Mockito.`when`(accountTransactionDao.get()).thenReturn(expectedTransactions)

        // Act
        accountTransactionRepository.get()

        // Assert
        Assertions.assertThat(expectedTransactions).hasSize(0)

        Unit
    }


    @Test
    fun get_whenAccountWithTheGivenIdExist_thenReturnsExpectedAccount() = runBlocking {
        // Arrange
        val accountTransactionId = 1
        val expectedTransaction = AccountTransactionDto(1, "Salary", 1, 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountTransactionDao.get(accountTransactionId)).thenReturn(expectedTransaction)

        // Act
        val accountTransaction = accountTransactionRepository.get(accountTransactionId)

        // Assert
        Assertions.assertThat(accountTransaction).isEqualTo(expectedTransaction)

        Unit
    }

    @Test
    fun getAccount_whenAccountWithTheGivenIdDoesNotExist_thenReturnsNull() = runBlocking {
        // Arrange
        val nonExistentAccountId = 0
        Mockito.`when`(accountTransactionDao.get(nonExistentAccountId)).thenReturn(null)

        // Act
        val accountTransaction = accountTransactionRepository.get(nonExistentAccountId)

        // Assert
        Assertions.assertThat(accountTransaction).isEqualTo(null)

        Unit
    }
}