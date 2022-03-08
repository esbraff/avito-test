package com.example.models

import org.jetbrains.exposed.sql.insert

class TransactionDAO {
    fun createTransfer(transfer: Transfer): Int =
        Transactions.insert {
            it[delta] = transfer.delta
            it[senderUUID] = transfer.senderUUID
            it[receiverUUID] = transfer.receiverUUID
        }.resultedValues!!.single()[Transactions.id].value

    fun createCorrection(correction: Correction): Int =
        Transactions.insert {
            it[delta] = correction.delta
            it[senderUUID] = correction.accountUUID
            it[receiverUUID] = null
        }.resultedValues!!.single()[Transactions.id].value
}