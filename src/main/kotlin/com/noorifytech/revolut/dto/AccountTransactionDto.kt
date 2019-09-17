package com.noorifytech.revolut.dto

import java.math.BigDecimal

data class AccountTransactionDto(
        val id: Int?,
        val purpose: String,
        val srcAccountId: Int,
        val destAccountId: Int,
        val amount: BigDecimal,
        val dateUpdated: Long?
)