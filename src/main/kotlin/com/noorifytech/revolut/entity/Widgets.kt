package com.noorifytech.revolut.entity

import org.jetbrains.exposed.sql.Table

object Widgets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 255)
    val quantity = integer("quantity")
    val dateUpdated = long("dateUpdated")
}
