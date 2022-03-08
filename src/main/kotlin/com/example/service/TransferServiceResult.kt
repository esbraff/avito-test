package com.example.service

import java.util.*

sealed class TransferServiceResult {
    sealed class Failed: TransferServiceResult() {
        class SenderDoesNotExist(val accountUUID: UUID): Failed()
        class NotEnoughMoney(val accountUUID: UUID): Failed()
        class NegativeDelta(val delta: Int): Failed()
        class TransactionAlreadyExists(val transactionUUID: UUID): Failed()
    }

    class Success(
        private val senderUUID: UUID,
        private val receiverUUID: UUID,
        private val delta: Int
    ): TransferServiceResult()
}