package com.example.service

import com.example.DatabaseFactory.dbQuery
import com.example.models.Account
import com.example.models.AccountDAO
import com.example.models.TransactionDAO
import com.example.models.Transfer
import java.util.UUID

class TransferService(private val accountDAO: AccountDAO, private val transactionDAO: TransactionDAO) {
    suspend fun makeTransfer(
        senderUUID: UUID,
        receiverUUID: UUID,
        amount: Int,
        idempotencyCode: UUID
    ): TransferServiceResult = dbQuery {
        val transaction = transactionDAO.findTransactionByUUID(idempotencyCode)

        if (transaction != null) {
            return@dbQuery TransferServiceResult.Failed.TransactionAlreadyExists(idempotencyCode)
        }

        val receiver = accountDAO.findAccountByUUID(receiverUUID)
        val sender = accountDAO.findAccountByUUID(senderUUID)
            ?: return@dbQuery TransferServiceResult.Failed.SenderDoesNotExist(senderUUID)

        if (amount < 0) {
            return@dbQuery TransferServiceResult.Failed.NegativeDelta(amount)
        }

        if (sender.balance < amount) {
            return@dbQuery TransferServiceResult.Failed.NotEnoughMoney(senderUUID)
        }

        if (receiver == null) {
            accountDAO.createAccount(Account(balance = amount, uuid = receiverUUID))
        } else {
            accountDAO.updateAccountByUUID(Account(balance = receiver.balance + amount, uuid = receiverUUID))
        }

        accountDAO.updateAccountByUUID(Account(balance = sender.balance - amount, uuid = senderUUID))
        transactionDAO.createTransfer(
            Transfer(delta = amount, senderUUID = senderUUID, receiverUUID = receiverUUID, uuid = idempotencyCode)
        )

        return@dbQuery TransferServiceResult.Success(senderUUID, receiverUUID, amount)
    }
}