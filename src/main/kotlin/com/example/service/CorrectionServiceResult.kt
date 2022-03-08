package com.example.service

import java.util.*

sealed class CorrectionServiceResult {
    sealed class Failed: CorrectionServiceResult() {
        class AccountDoesNotExist(val accountUUID: UUID): Failed()
        class NotEnoughMoney(val accountUUID: UUID): Failed()
        class NegativeDelta(val delta: Int): Failed()
    }

    sealed class Success: CorrectionServiceResult() {
        class Credited(val accountUUID: UUID, val amount: Int): Success()
        class ChargedOff(val accountUUID: UUID, val amount: Int): Success()
    }
}