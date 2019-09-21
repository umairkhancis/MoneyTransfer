package com.noorifytech.revolut.service

import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.exception.TransactionFailedException
import com.noorifytech.revolut.repository.AccountRepository
import com.noorifytech.revolut.repository.AccountTransactionRepository
import com.noorifytech.revolut.service.impl.AccountTransactionServiceImpl
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal

class AccountTransactionServiceTest {

    @Mock
    private lateinit var accountTransactionRepository: AccountTransactionRepository
    @Mock
    private lateinit var accountRepository: AccountRepository
    private lateinit var accountTransactionService: AccountTransactionService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        accountTransactionService = AccountTransactionServiceImpl(accountTransactionRepository, accountRepository)
    }

    @Test
    fun getTransactions_whenTwoTransactionsExist_thenReturnsTransactionsResponseSuccessfully() = runBlocking {
        // Arrange
        val expectedTransactions = ArrayList<AccountTransactionDto>()
        val transaction1 = AccountTransactionDto(1, "Salary", 1, 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        val transaction2 = AccountTransactionDto(2, "Salary", 2, 3, BigDecimal.valueOf(100), System.currentTimeMillis())
        expectedTransactions.add(transaction1)
        expectedTransactions.add(transaction2)
        Mockito.`when`(accountTransactionRepository.get()).thenReturn(expectedTransactions)

        // Act
        val transactionsResponse = accountTransactionService.getTransactions()

        // Assert
        assertThat(transactionsResponse.data).hasSize(2)
        assertThat(transactionsResponse.data?.get(0)).isEqualTo(transaction1)
        assertThat(transactionsResponse.data?.get(1)).isEqualTo(transaction2)
        assertThat(transactionsResponse.code).isEqualTo(200)
        assertThat(transactionsResponse.msg).isEqualTo("OK")

        Unit
    }

    @Test
    fun getTransactions_whenNoTransactionExists_thenReturnsEmptyTransactionsResponseSuccessfully() = runBlocking {
        // Arrange
        val expectedTransactions = ArrayList<AccountTransactionDto>()
        Mockito.`when`(accountTransactionRepository.get()).thenReturn(expectedTransactions)

        // Act
        val transactionsResponse = accountTransactionService.getTransactions()

        // Assert
        assertThat(transactionsResponse.data).hasSize(0)
        assertThat(transactionsResponse.code).isEqualTo(200)
        assertThat(transactionsResponse.msg).isEqualTo("OK")

        Unit
    }

    @Test
    fun getTransaction_whenTransactionWithTheGivenIdExists_thenReturnsExpectedTransaction() = runBlocking {
        // Arrange
        val id = 1
        val expectedTransaction = AccountTransactionDto(id, "Salary", 1, 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountTransactionRepository.get(id)).thenReturn(expectedTransaction)

        // Act
        val transactionResponse = accountTransactionService.getTransaction(id)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(expectedTransaction)
        assertThat(transactionResponse.code).isEqualTo(200)
        assertThat(transactionResponse.msg).isEqualTo("OK")

        Unit
    }

    @Test
    fun getTransaction_whenTransactionDoesNotExist_thenReturns404NotFoundResponse() = runBlocking {
        // Arrange
        val id = 0
        Mockito.`when`(accountTransactionRepository.get(id)).thenReturn(null)

        // Act
        val transactionResponse = accountTransactionService.getTransaction(id)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(null)
        assertThat(transactionResponse.code).isEqualTo(404)
        assertThat(transactionResponse.msg).isEqualTo("Not Found")

        Unit
    }

    @Test
    fun transfer_whenBothSrcAndDestAccountsExist_thenReturnsTransactionSuccessfulResponse() = runBlocking {
        // Arrange
        val data = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(100), null)
        val srcAccount = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        val destAccount = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        val expectedTransaction = AccountTransactionDto(1, "Salary", 1, 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountRepository.get(data.srcAccountId)).thenReturn(srcAccount)
        Mockito.`when`(accountRepository.get(data.destAccountId)).thenReturn(destAccount)
        Mockito.`when`(accountTransactionRepository.create(data)).thenReturn(expectedTransaction)

        // Act
        val transactionResponse = accountTransactionService.transfer(data)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(expectedTransaction)
        assertThat(transactionResponse.code).isEqualTo(201)
        assertThat(transactionResponse.msg).isEqualTo("Transaction successfully completed!")

        Unit
    }

    @Test
    fun transfer_whenSrcAccountDoesNotExistButOnlyDestAccountDoesExist_thenReturns404NotFoundErrorResponse() = runBlocking {
        // Arrange
        val data = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(100), null)
        val destAccount = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountRepository.get(data.srcAccountId)).thenReturn(null)
        Mockito.`when`(accountRepository.get(data.destAccountId)).thenReturn(destAccount)

        // Act
        val transactionResponse = accountTransactionService.transfer(data)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(null)
        assertThat(transactionResponse.code).isEqualTo(404)
        assertThat(transactionResponse.msg).isEqualTo("Source account not found!")

        Unit
    }

    @Test
    fun transfer_whenDestAccountDoesNotExistButOnlySrcAccountDoesExist_thenReturns404NotFoundErrorResponse() = runBlocking {
        // Arrange
        val data = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(100), null)
        val srcAccount = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountRepository.get(data.srcAccountId)).thenReturn(srcAccount)
        Mockito.`when`(accountRepository.get(data.destAccountId)).thenReturn(null)

        // Act
        val transactionResponse = accountTransactionService.transfer(data)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(null)
        assertThat(transactionResponse.code).isEqualTo(404)
        assertThat(transactionResponse.msg).isEqualTo("Destination account not found!")

        Unit
    }

    @Test
    fun transfer_whenTransactionFailedDueToSomeReason_thenReturns500ErrorResponse() = runBlocking {
        // Arrange
        val data = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(100), null)
        val srcAccount = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100), System.currentTimeMillis())
        val destAccount = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100), System.currentTimeMillis())
        Mockito.`when`(accountRepository.get(data.srcAccountId)).thenReturn(srcAccount)
        Mockito.`when`(accountRepository.get(data.destAccountId)).thenReturn(destAccount)
        Mockito.`when`(accountTransactionRepository.create(data)).thenAnswer { throw (TransactionFailedException("Insufficient Balance!")) }
//        Mockito.doThrow(TransactionFailedException::class.java).`when`(accountTransactionRepository).create(data)

        // Act
        val transactionResponse = accountTransactionService.transfer(data)

        // Assert
        assertThat(transactionResponse.data).isEqualTo(null)
        assertThat(transactionResponse.code).isEqualTo(500)
        assertThat(transactionResponse.msg).isEqualTo("Transaction Failed due to Insufficient Balance!")

        Unit
    }
}