package com.noorifytech.revolut.controller

import com.noorifytech.revolut.service.AccountService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.account(service: AccountService) {

    route("/account") {

        get("/") {
            call.respond(service.getAllAccounts())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id");
            val response = service.getAccount(id)
            if (response.data == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(response)
        }
    }
}
