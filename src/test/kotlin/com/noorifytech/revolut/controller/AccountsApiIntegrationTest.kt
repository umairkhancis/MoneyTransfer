package com.noorifytech.revolut.controller

import com.noorifytech.revolut.common.ServerTest
import com.noorifytech.revolut.dto.AccountDto
import com.noorifytech.revolut.dto.Response
import io.restassured.RestAssured.get
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Integration Test from API controller to Database.
 */
class AccountsApiIntegrationTest : ServerTest() {

    @Test
    fun getAccountsApi_returnsAllAccounts() {
        // Arrange
        val account1 = AccountDto(1, "Umair Ahmed Khan", 1, BigDecimal.valueOf(100.0), System.currentTimeMillis())
        val account2 = AccountDto(2, "Aamir Ahmed Khan", 2, BigDecimal.valueOf(100.0), System.currentTimeMillis())
        val account3 = AccountDto(3, "Usman Ahmed Khan", 3, BigDecimal.valueOf(100.0), System.currentTimeMillis())

        // Act
        val response = get("/account")
                .then()
                .statusCode(200)
                .extract().to<Response<List<AccountDto>>>()
        val accounts = response.data

        // Assert
        assertThat(accounts).hasSize(3)
        assertThat(accounts).extracting("name").containsExactlyInAnyOrder(account1.name, account2.name, account3.name)
        assertThat(accounts).extracting("userId").containsExactlyInAnyOrder(account1.userId, account2.userId, account3.userId)
    }

    @Test
    fun getAccountApi_returnsSpecificAccountWithTheGivenId() {
        get("/account/{id}", 1)
                .then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.userId", equalTo(1))
                .body("data.name", equalTo("Umair Ahmed Khan"))
    }

    @Test
    fun getInvalidAccount_returns404() {
        get("/account/{id}", "-1")
                .then()
                .statusCode(404)
    }
}