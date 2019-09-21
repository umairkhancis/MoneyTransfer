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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal


/**
 * Integration Test from API controller to Database.
 */
class AccountTransactionsApiIntegrationTest : ServerTest() {

    @AfterEach
    fun tear() {
        transaction {
            AccountTransactions.deleteAll()
        }
    }

    @Test
    fun getAccountTransactionsApi_whenNoTransactionsInTheSystem_returnsEmptyListOfTransactions() {
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
    fun getAccountTransactionsApi_returnsListOfTransactions() {
        // Arrange
        val transaction1 = AccountTransactionDto(null, "Umair Ahmed Khan", 1, 2, BigDecimal.valueOf(10), System.currentTimeMillis())
        val transaction2 = AccountTransactionDto(null, "Aamir Ahmed Khan", 2, 3, BigDecimal.valueOf(10), System.currentTimeMillis())
        val transaction3 = AccountTransactionDto(null, "Usman Ahmed Khan", 3, 1, BigDecimal.valueOf(10), System.currentTimeMillis())
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
    fun getAccountTransactionApi_returnsSpecificTransactionWithTheGivenId() {
        // Arrange
        val transaction = AccountTransactionDto(null, "Salary", 1, 2, BigDecimal.valueOf(10), System.currentTimeMillis())
        addTransaction(transaction)
        val response = get("/transaction")
                .then()
                .statusCode(200)
                .extract()
                .response()
        val latestTransactionId = response.jsonPath().getInt("data[0].id")

        // Act & Assert
        get("/transaction/{id}", latestTransactionId)
                .then()
                .statusCode(200)
                .body("data.srcAccountId", equalTo(transaction.srcAccountId))
                .body("data.destAccountId", equalTo(transaction.destAccountId))
                .body("data.purpose", equalTo(transaction.purpose))
    }

    @Test
    fun transferApi_whenTransferAmountIsLessThanCurrentBalanceOfSrcAccount_thenCheckBalanceAfterSuccessfulTransactionForBothSrcAndDestAccount2() {
        // Arrange
        val srcAccountId = 1
        val destAccountId = 2
        val currentSrcAccountBalance = get("/account/{id}", srcAccountId)
                .then()
                .extract()
                .response()
                .jsonPath()
                .getInt("data.balance")
        val currentDestAccountBalance = get("/account/{id}", destAccountId)
                .then()
                .extract()
                .response()
                .jsonPath()
                .getInt("data.balance")
        val transferAmount = currentSrcAccountBalance - 25L
        val transaction = AccountTransactionDto(null, "Salary", srcAccountId, destAccountId, BigDecimal.valueOf(transferAmount), System.currentTimeMillis())

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
                .body("data.balance", equalTo((currentDestAccountBalance - transferAmount).toInt()))
        get("/account/{id}", transaction.destAccountId)
                .then()
                .statusCode(200)
                .body("data.balance", equalTo((currentDestAccountBalance + transferAmount).toInt()))
    }

    @Test
    fun transferApi_whenTransferAmountIsMoreThanCurrentBalanceOfSrcAccount_thenCheckBalanceAfterFailedTransactionForBothSrcAndDestAccountItShouldBeSameAsBefore() {
        // Arrange
        val srcAccountId = 1
        val destAccountId = 2
        val currentSrcAccountBalance = get("/account/{id}", srcAccountId)
                .then()
                .extract()
                .response()
                .jsonPath()
                .getInt("data.balance")
        val currentDestAccountBalance = get("/account/{id}", destAccountId)
                .then()
                .extract()
                .response()
                .jsonPath()
                .getInt("data.balance")
        val transferAmount = currentSrcAccountBalance + 50L
        val transaction = AccountTransactionDto(null, "Salary", srcAccountId, destAccountId, BigDecimal.valueOf(transferAmount), System.currentTimeMillis())

        // Act
        given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .When()
                .post("/transaction/transfer")
                .then()
                .statusCode(500)

        // Assert
        get("/account/{id}", transaction.srcAccountId)
                .then()
                .statusCode(200)
                .body("data.balance", equalTo(currentSrcAccountBalance))
        get("/account/{id}", transaction.destAccountId)
                .then()
                .statusCode(200)
                .body("data.balance", equalTo(currentDestAccountBalance))
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