package com.noorifytech.revolut.dto

import java.math.BigDecimal

data class AccountDto(
        val id: Int,
        val name: String,
        val userId: Int,
        val balance: BigDecimal,
        val dateUpdated: Long
)