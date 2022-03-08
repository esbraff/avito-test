package com.example.service

import com.example.DatabaseFactory.dbQuery
import com.example.models.AccountDAO
import java.util.UUID

class AccountService(private val accountDAO: AccountDAO) {
    suspend fun getAccountBalance(accountUUID: UUID): AccountServiceResult = dbQuery {
        val account = accountDAO.findAccountByUUID(accountUUID)
            ?: return@dbQuery AccountServiceResult.Failed.AccountDoesNotExist(accountUUID)

        return@dbQuery AccountServiceResult.Success.Balance(balance = account.balance)
    }
}