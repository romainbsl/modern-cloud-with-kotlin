package org.kodein.api.demo

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.kodein.api.demo.model.User
import org.kodein.api.demo.service.ItemService
import org.kodein.di.DI
import org.kodein.di.erased.instance
import org.kodein.di.ktor.controller.DIController
import org.kodein.di.ktor.di

class PublicItemsController(application: Application) : DIController {
    override val di: DI = di { application }
    val itemService : ItemService by instance()

    override fun Route.getRoutes() {
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

class ProtectedItemsController(application: Application) : DIController {
    override val di: DI = di { application }
    val itemService : ItemService by instance()

    override fun Route.getRoutes() {
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
