package com.example.models

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.UUID

class AccountDAO {
    fun createAccount(account: Account): Int =
        Accounts.insert {
            it[balance] = account.balance
            it[uuid] = account.uuid
        }.resultedValues!!.single()[Accounts.id].value

    fun updateAccountByUUID(account: Account) =
        Accounts.update({ Accounts.uuid eq account.uuid }) {
            it[balance] = account.balance
        }

    fun findAccountByUUID(uuid: UUID): Account? {
        return Accounts.select { Accounts.uuid eq uuid }.map { Accounts.rowToAccount(it) }.singleOrNull()
    }
}