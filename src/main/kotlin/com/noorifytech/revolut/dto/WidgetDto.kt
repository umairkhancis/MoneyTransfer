package com.noorifytech.revolut.dto

data class WidgetDto(
        val id: Int,
        val name: String,
        val quantity: Int,
        val dateUpdated: Long
)


/**
 * For api input data parameter purpose
 */
data class NewWidgetDto(
        val id: Int?,
        val name: String,
        val quantity: Int
)
