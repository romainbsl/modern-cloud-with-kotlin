package org.kodein.api.demo.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.date

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)
    var username by Users.username
    var password by Users.password
}
class ItemEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemEntity>(Items)
    var label by Items.label
    var dueDate by Items.dueDate
    var user by UserEntity optionalReferencedOn Items.userId
}

object Users : IntIdTable() {
    val username = varchar("username", 50)
    val password = varchar("password", 50)
}
object Items : IntIdTable() {
    val label = varchar("label", 50)
    val dueDate = date("dueDate")
    val userId = reference("user_id", Users, ReferenceOption.SET_NULL).nullable()
}