package com.noorifytech.revolut.entity

import org.jetbrains.exposed.sql.Table

object AccountTransactions : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val purpose = varchar("name", 255)
    val srcAccountId = integer("src_account_id").references(Accounts.id)
    val destAccountId = integer("dest_account_id").references(Accounts.id)
    val amount = decimal("amount", 15, 0)
    val dateUpdated = long("dateUpdated")
}
