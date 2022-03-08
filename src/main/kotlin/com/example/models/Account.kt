package com.example.models

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object Accounts: IntIdTable() {
    val balance = integer("balance")
    val uuid = uuid("uuid").uniqueIndex()
}

data class Account(
    val id: Int = 0,
    val balance: Int,
    val uuid: UUID
)

fun Accounts.rowToAccount(row: ResultRow): Account = Account(
    id = row[id].value,
    balance = row[balance],
    uuid = row[uuid]
)