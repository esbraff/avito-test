package com.example.models

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

class TransactionDAO {
    fun createTransfer(transfer: Transfer): Int =
        Transactions.insert {
            it[delta] = transfer.delta
            it[senderUUID] = transfer.senderUUID
            it[receiverUUID] = transfer.receiverUUID
            it[uuid] = transfer.uuid
        }.resultedValues!!.single()[Transactions.id].value

    fun createCorrection(correction: Correction): Int =
        Transactions.insert {
            it[delta] = correction.delta
            it[senderUUID] = correction.accountUUID
            it[receiverUUID] = null
            it[uuid] = correction.uuid
        }.resultedValues!!.single()[Transactions.id].value

    fun findTransactionByUUID(uuid: UUID): Transaction? {
        return Transactions.select { Transactions.uuid eq uuid }.map { Transactions.rowToTransaction(it) }.singleOrNull()
    }
}