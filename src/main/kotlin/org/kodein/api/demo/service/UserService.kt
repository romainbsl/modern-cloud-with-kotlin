package org.kodein.api.demo.service

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.kodein.api.demo.database.UserEntity
import org.kodein.api.demo.database.Users
import org.kodein.api.demo.database.dbQuery
import org.kodein.api.demo.model.Item
import org.kodein.api.demo.model.User

class UserService() {
    suspend fun check(user: User): User? = dbQuery {
        val query = Users.select {
            (Users.username eq user.username) and
                    (Users.password eq user.password)
        }.singleOrNull()

        if (query != null) {
            UserEntity.wrapRow(query).asDTO()
        } else null
    }

    suspend fun delete(id: Int) = dbQuery {
        UserEntity.findById(id)?.delete()
    }
}

internal fun UserEntity.asDTO(items: List<Item>? = null) = User(
    id = id.value,
    username = username,
    password = password,
    items = items
)
