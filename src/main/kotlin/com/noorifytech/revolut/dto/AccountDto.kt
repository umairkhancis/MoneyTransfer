package com.noorifytech.revolut.dto

import java.math.BigDecimal

data class AccountDto(
        val id: Int,
        val name: String,
        val userId: Int,
        val balance: BigDecimal,
        val dateUpdated: Long
)

/**
 * For api input data parameter purpose
 */
data class NewAccountDto(
        val id: Int?,
        val name: String,
        val userId: Int,
        val balance: BigDecimal
)
