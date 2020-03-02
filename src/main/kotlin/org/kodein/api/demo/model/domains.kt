package org.kodein.api.demo.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.auth.Principal
import io.ktor.auth.UserPasswordCredential
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
    val id: Int = -1,
    val username: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val password: String,
    val items: List<Item>? = null
): Principal

fun UserPasswordCredential.toUser() = User(username = this.name, password = this.password)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item(
    val id: Int = -1,
    val label: String,
    val dueDate: LocalDate,
    val user: User? = null
)