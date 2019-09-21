package com.noorifytech.revolut.dto

data class Response<out T>(val data: T?, val code: Int, val msg: String? = null) {
    constructor() : this(null, 0, null)
}