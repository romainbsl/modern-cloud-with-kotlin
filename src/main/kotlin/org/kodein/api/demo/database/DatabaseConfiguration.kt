package org.kodein.api.demo.database

import com.qovery.client.DatabaseConfiguration
import com.qovery.client.Qovery
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import kotlin.streams.toList

object DatabaseConfig {
    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.drop(Users, Items)
            SchemaUtils.create(Users, Items)

            val johnId = Users.insert {
                it[username] = "john"
                it[password] = "pwd"
            } get Users.id
            val janeId = Users.insert {
                it[username] = "jane"
                it[password] = "pwd"
            } get Users.id

            Items.insert {
                it[label] = "Pickup the kids"
                it[dueDate] = LocalDate.now().plusDays(1)
            }
            Items.insert {
                it[label] = "Plan the next holidays"
                it[dueDate] = LocalDate.now().plusDays(5)
            }
            Items.insert {
                it[label] = "Prepare the Talking.Kt presentation"
                it[dueDate] = LocalDate.now().minusDays(1)
                it[userId] = johnId
            }
            Items.insert {
                it[label] = "Conf call with client"
                it[dueDate] = LocalDate.now().plusDays(1)
                it[userId] = johnId
            }
            Items.insert {
                it[label] = "Find some new zero waste inspiration"
                it[dueDate] = LocalDate.now().minusDays(3)
                it[userId] = janeId
            }
            Items.insert {
                it[label] = "Call Mom!"
                it[dueDate] = LocalDate.now().plusDays(5)
                it[userId] = janeId
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val qovery = Qovery()

        val databaseConfiguration =
            qovery.listDatabaseConfiguration()?.toList()
                ?.find { it.type == DatabaseType.POSTGRESQL.value }
                ?: getLocalDataSource()

        val host = databaseConfiguration.host
        val port = databaseConfiguration.port
        val username = databaseConfiguration.username
        val password = databaseConfiguration.password

        val config = HikariConfig()

        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = "jdbc:postgresql://$host:$port/postgres"
        config.username = username
        config.password = password
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()

        return HikariDataSource(config)
    }

    private fun getLocalDataSource(): DatabaseConfiguration {
        return DatabaseConfiguration(
            DatabaseType.POSTGRESQL.value,
            "postgres-cloud-mordern-with-kotlin",
            "localhost",
            5432,
            "postgres",
            "docker",
            "11.5"
        )
    }
}

enum class DatabaseType(val value: String) {
    POSTGRESQL("POSTGRESQL"),
    MYSQL("MYSQL"),
}

suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }