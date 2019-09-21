package com.noorifytech.revolut.controller

import com.noorifytech.revolut.common.ServerTest
import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.dto.Response
import com.noorifytech.revolut.entity.AccountTransactions
import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal


/**
 * Integration Test from API controller to Database.
 */
class AccountTransactionsApiIntegrationTest : ServerTest() {

    @BeforeEach
    fun setup() {
        transaction {
            AccountTransactions.deleteAll()
        }
    }

    @AfterEach
    fun tear() {
        transaction {
            AccountTransactions.deleteAll()
        }
    }

    @Test
    fun getAccountTransactions_returnsListOfTransactions() {
        // Arrange
        val transaction1 = AccountTransactionDto(null, "Umair Ahmed Khan", 1, 2, BigDecimal.valueOf(50), System.currentTimeMillis())
        val transaction2 = AccountTransactionDto(null, "Aamir Ahmed Khan", 2, 3, BigDecimal.valueOf(50), System.currentTimeMillis())
        val transaction3 = AccountTransactionDto(null, "Usman Ahmed Khan", 3, 2, BigDecimal.valueOf(50), System.currentTimeMillis())
        addTransaction(transaction1)
        addTransaction(transaction2)
        addTransaction(transaction3)

        // Act
        val response = get("/transaction")
                .then()
                .statusCode(200)
                .extract().to<Response<List<AccountTransactionDto>>>()
        val accounts = response.data

        // Assert
        assertThat(accounts).hasSize(3)
        assertThat(accounts).extracting("srcAccountId").containsExactlyInAnyOrder(transaction1.srcAccountId, transaction2.srcAccountId, transaction3.srcAccountId)
        assertThat(accounts).extracting("destAccountId").containsExactlyInAnyOrder(transaction1.destAccountId, transaction2.destAccountId, transaction3.destAccountId)
        assertThat(accounts).extracting("purpose").containsExactlyInAnyOrder(transaction1.purpose, transaction2.purpose, transaction3.purpose)
    }

    @Test
    fun getAccountTransactions_whenNoTransactionsInTheSystem_returnsEmptyListOfTransactions() {
        // Arrange
        // Act
        val response = get("/transaction")
                .then()
                .statusCode(200)
                .extract().to<Response<List<AccountTransactionDto>>>()
        val transactionsList = response.data

        // Assert
        assertThat(transactionsList).hasSize(0)
    }

    @Test
    fun getAccountTransaction_returnsSpecificTransactionWithTheGivenId() {
        // Arrange
        val transaction = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(50), System.currentTimeMillis())
        addTransaction(transaction)

        val responseType = Response<List<AccountTransactionDto>>()
        val response = get("/transaction")
                .then()
                .statusCode(200)
                .extract().`as`(responseType.javaClass)

        // Act & Assert
        get("/transaction/{id}", 1)
                .then()
                .statusCode(200)
                .body("data.srcAccountId", equalTo(transaction.srcAccountId))
                .body("data.destAccountId", equalTo(transaction.destAccountId))
                .body("data.purpose", equalTo(transaction.purpose))
    }

    @Test
    fun checkBalanceAfterSuccessfulTransaction_whenSrcAndDestAccountsInitialBalanceIs100AndTransferAmountIs25_thenSrcAccountBalanceShouldBe75AndDestAccountBalanceShouldBe125() {
        // Arrange
        val transaction = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(25), System.currentTimeMillis())

        // Act
        given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .When()
                .post("/transaction/transfer")
                .then()
                .statusCode(201)
                .body("data.srcAccountId", equalTo(transaction.srcAccountId))
                .body("data.destAccountId", equalTo(transaction.destAccountId))
                .body("data.purpose", equalTo(transaction.purpose))

        // Assert
        get("/account/{id}", transaction.srcAccountId)
                .then()
                .statusCode(200)
                .body("data.balance", equalTo(75))
        get("/account/{id}", transaction.destAccountId)
                .then()
                .statusCode(200)
                .body("data.balance", equalTo(125))
    }

    private fun addTransaction(transaction: AccountTransactionDto) {
        given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .When()
                .post("/transaction/transfer")
    }

    @Nested
    inner class ErrorCases {
        @Test
        fun testGetInvalidAccountTransaction() {
            get("/transaction/{id}", "-1")
                    .then()
                    .statusCode(404)
        }
    }

}