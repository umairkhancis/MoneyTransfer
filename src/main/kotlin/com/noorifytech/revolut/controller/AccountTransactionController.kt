package com.noorifytech.revolut.controller

import com.noorifytech.revolut.dto.AccountTransactionDto
import com.noorifytech.revolut.service.AccountTransactionService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.accountTransaction(service: AccountTransactionService) {

    route("/transaction") {

        get("/") {
            call.respond(service.getTransactions())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id");
            val response = service.getTransaction(id)
            if (response.data == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(response)
        }

        post("/transfer") {
            val accountTransaction = call.receive<AccountTransactionDto>()
            val response = service.transfer(accountTransaction)
            if (response.data == null) call.respond(HttpStatusCode.fromValue(response.code), response)
            else call.respond(HttpStatusCode.fromValue(response.code), response.data)
        }
    }
}
