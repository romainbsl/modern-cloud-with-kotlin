package org.kodein.api.demo

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.routing.routing
import org.kodein.api.demo.model.toUser
import org.kodein.api.demo.service.UserService
import org.kodein.di.erased.instance
import org.kodein.di.ktor.controller.controller
import org.kodein.di.ktor.di

fun Application.auth() {
    val userService: UserService by di().instance()

    install(Authentication) {
        basic("basicAuth") {
            validate {
                userService.check(it.toUser())
            }
        }
    }
}

fun Application.routes(): Unit {
    routing {
        controller { PublicItemsController(application) }
        controller { ProtectedItemsController(application) }
    }
}