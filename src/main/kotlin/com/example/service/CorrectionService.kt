package com.example.service

import com.example.models.AccountDAO
import com.example.models.TransactionDAO
import com.example.DatabaseFactory.dbQuery
import com.example.models.Account
import com.example.models.Correction
import java.util.UUID

class CorrectionService(private val accountDAO: AccountDAO, private val transactionDAO: TransactionDAO) {
    suspend fun creditMoney(accountUUID: UUID, amount: Int): CorrectionServiceResult = dbQuery {
        val account = accountDAO.findAccountByUUID(accountUUID)

        if (account == null) {
            accountDAO.createAccount(Account(balance = 0, uuid = accountUUID))
        }

        if (amount < 0) {
            return@dbQuery CorrectionServiceResult.Failed.NegativeDelta(amount)
        }

        accountDAO.updateAccountByUUID(Account(balance = (account?.balance ?: 0) + amount, uuid = accountUUID))
        transactionDAO.createCorrection(Correction(delta = amount, accountUUID = accountUUID))

        return@dbQuery CorrectionServiceResult.Success.Credited(accountUUID, amount)
    }

    suspend fun chargeOffMoney(accountUUID: UUID, amount: Int): CorrectionServiceResult = dbQuery {
        val account = accountDAO.findAccountByUUID(accountUUID)

        if (amount < 0) {
            return@dbQuery CorrectionServiceResult.Failed.NegativeDelta(amount)
        }

        if (account == null) {
            return@dbQuery CorrectionServiceResult.Failed.AccountDoesNotExist(accountUUID)
        }

        if (account.balance < amount) {
            return@dbQuery CorrectionServiceResult.Failed.NotEnoughMoney(accountUUID)
        }

        accountDAO.updateAccountByUUID(Account(balance = account.balance - amount, uuid = accountUUID))
        transactionDAO.createCorrection(Correction(delta = -amount, accountUUID = accountUUID))

        return@dbQuery CorrectionServiceResult.Success.ChargedOff(accountUUID, amount)
    }
}