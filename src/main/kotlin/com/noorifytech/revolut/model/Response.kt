package com.noorifytech.revolut.model

data class Response<T>(val data: T?, val code: Int, val msg: String? = null)