package org.kodein.api.demo

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.jackson.jackson
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2
import org.kodein.api.demo.database.DatabaseConfig
import org.kodein.api.demo.service.ItemService
import org.kodein.api.demo.service.UserService
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.kodein.di.ktor.di
import java.text.SimpleDateFormat

@Suppress("unused")
fun Application.load() {
    routing {
        route("/") {
            get {
                call.respondHtml {
                    body {
                        h1 { +"Hello, Kotlin lovers!" }
                        h2 { +"Let's code!" }
                    }
                }
            }
        }
    }
}

fun Application.configuration() {
    DatabaseConfig.init()

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            registerModule(JavaTimeModule())
            dateFormat = SimpleDateFormat()
        }
    }

    di {
        bind() from singleton { ItemService() }
        bind() from singleton { UserService() }
    }
}
