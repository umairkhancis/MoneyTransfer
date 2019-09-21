package com.noorifytech.revolut.entity

import org.jetbrains.exposed.sql.Table

object Accounts : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val userId = integer("user_id").references(Users.id)
    val balance = decimal("balance", 15, 0)
    val dateUpdated = long("dateUpdated")
}
