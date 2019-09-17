package com.noorifytech.revolut.dto

data class UserDto(
        val id: Int,
        val name: String,
        val email: String,
        val dateUpdated: Long
)

/**
 * For api input data parameter purpose
 */
data class NewUserDto(
        val id: Int?,
        val name: String,
        val email: String
)
