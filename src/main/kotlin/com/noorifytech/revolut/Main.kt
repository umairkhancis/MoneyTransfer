package com.noorifytech.revolut

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import com.noorifytech.revolut.service.DatabaseFactory
import com.noorifytech.revolut.service.WidgetService
import com.noorifytech.revolut.web.widget

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    DatabaseFactory.init()

    val widgetService = WidgetService()

    install(Routing) {
        widget(widgetService)
    }

}

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080, module = Application::module).start(wait = true)
}