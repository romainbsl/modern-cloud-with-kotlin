package org.kodein.api.demo.service

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.kodein.api.demo.database.ItemEntity
import org.kodein.api.demo.database.Items
import org.kodein.api.demo.database.Users
import org.kodein.api.demo.database.dbQuery
import org.kodein.api.demo.model.Item

class ItemService {
    suspend fun findAll(userId: Int? = null) = dbQuery {
        val query = if (userId == null)
            Items.select { Items.userId.isNull() }
        else
            Items.leftJoin(Users).select {
                Items.userId.isNull() or
                        ((Items.userId eq Users.id) and (Users.id eq userId)) }
        ItemEntity.wrapRows(query).map { it.asDTO() }
    }

    suspend fun findById(id: Int, userId: Int? = null) = dbQuery {
        val query = if (userId == null) Items.select { (Items.id eq id) and Items.userId.isNull() }.singleOrNull()
        else Items.select { (Items.id eq id) and Items.userId.isNull() }.singleOrNull()
        if (query != null)
            ItemEntity.wrapRow(query).asDTO()
        else null
    }

    suspend fun findByUserId(userId: Int) =
        dbQuery {
            val query = Items.innerJoin(Users)
                .select { (Items.userId eq Users.id) and (Users.id eq userId) }
            ItemEntity.wrapRows(query).map { it.asDTO() }
        }

    suspend fun delete(id: Int) = dbQuery {
        ItemEntity.findById(id)?.delete()
    }
}

internal fun ItemEntity.asDTO() = Item(
    id = this.id.value,
    label = this.label,
    dueDate = this.dueDate
)