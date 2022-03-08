package com.example.service

import java.util.UUID

sealed class AccountServiceResult {
    sealed class Failed: AccountServiceResult() {
        class AccountDoesNotExist(val accountUUID: UUID): Failed()
    }

    sealed class Success: AccountServiceResult() {
        class Balance(val balance: Int): Success()
    }
}
