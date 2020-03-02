package org.kodein.api.demo

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import org.kodein.api.demo.model.User
import org.kodein.api.demo.model.toUser
import org.kodein.api.demo.service.ItemService
import org.kodein.api.demo.service.UserService

fun Application.todolist() {
    routing {
        route("items") {
            val itemService = ItemService()

            get("/all") {
                call.respond(itemService.findAll())
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toInt()
                if (id != null) {
                    val item = itemService.findById(id)
                        if (item != null) call.respond(item)
                    else call.respond(HttpStatusCode.NotFound)
                } else
                    call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
fun Application.protected() {
    install(Authentication) {
        basic("basicAuth") {
            validate {
                val userService = UserService()
                userService.check(it.toUser())
            }
        }
    }

    routing {
        authenticate("basicAuth") {
            route("/protected"){
                get("/user") {
                    val principal = call.principal<User>()
                    call.respondText("Hello ${principal?.username ?: "Nobody"}!")
                }
                route("/items") {
                    get("/all") {
                        val principal = call.principal<User>()
                        if (principal != null) {
                            val itemService = ItemService()
                            call.respond(
                                HttpStatusCode.OK,
                                itemService.findAll(principal.id)
                            )
                        }
                    }
                }
            }
        }
    }
}