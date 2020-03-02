package org.kodein.api.demo

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.h2

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