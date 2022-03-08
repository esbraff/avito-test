package com.example.models

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update
import java.util.UUID

object Transactions: IntIdTable() {
    val delta = integer("delta")
    val senderUUID = reference("senderuuid", Accounts.uuid)
    val receiverUUID = reference("receiveruuid", Accounts.uuid).nullable()
    val uuid = uuid("uuid").nullable()
}

sealed class Transaction

data class Correction(
    val id: Int = 0,
    val delta: Int,
    val accountUUID: UUID,
    val uuid: UUID?
) : Transaction()

data class Transfer(
    val id: Int = 0,
    val delta: Int,
    val senderUUID: UUID,
    val receiverUUID: UUID,
    val uuid: UUID?
) : Transaction()

fun Transactions.rowToTransaction(row: ResultRow): Transaction =
    if (row[receiverUUID] != null) {
        rowToTransfer(row);
    } else {
        rowToCorrection(row);
    }

fun Transactions.rowToCorrection(row: ResultRow): Correction = Correction(
    id = row[id].value,
    delta = row[delta],
    accountUUID = row[senderUUID],
    uuid = row[uuid]
)

fun Transactions.rowToTransfer(row: ResultRow): Transfer = Transfer(
    id = row[id].value,
    delta = row[delta],
    senderUUID = row[senderUUID],
    receiverUUID = row[receiverUUID]!!,
    uuid = row[uuid]
)