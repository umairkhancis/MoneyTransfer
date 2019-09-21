package com.noorifytech.revolut

import com.fasterxml.jackson.databind.SerializationFeature
import com.noorifytech.revolut.controller.account
import com.noorifytech.revolut.controller.accountTransaction
import com.noorifytech.revolut.dao.impl.AccountDaoImpl
import com.noorifytech.revolut.dao.impl.AccountTransactionDaoImpl
import com.noorifytech.revolut.dao.impl.db.impl.H2Database
import com.noorifytech.revolut.mapper.AccountMapper
import com.noorifytech.revolut.mapper.AccountTransactionMapper
import com.noorifytech.revolut.repository.impl.AccountRepositoryImpl
import com.noorifytech.revolut.repository.impl.AccountTransactionRepositoryImpl
import com.noorifytech.revolut.service.impl.AccountServiceImpl
import com.noorifytech.revolut.service.impl.AccountTransactionServiceImpl
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    H2Database.init()

    install(Routing) {
        account(AccountServiceImpl(AccountRepositoryImpl(AccountDaoImpl(H2Database, AccountMapper))))
        accountTransaction(
                AccountTransactionServiceImpl(
                        AccountTransactionRepositoryImpl(AccountTransactionDaoImpl(H2Database, AccountMapper, AccountTransactionMapper)),
                        AccountRepositoryImpl(AccountDaoImpl(H2Database, AccountMapper))
                )
        )
    }

}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            embeddedServer(Netty, 8080, watchPaths = listOf("Main"), module = Application::module)
                    .start(wait = true)
        }
    }
}