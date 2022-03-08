package com.example

import com.example.models.AccountDAO
import com.example.models.TransactionDAO
import com.example.service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import java.util.*
import kotlin.reflect.typeOf

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    DatabaseFactory.init()
    val accountDAO = AccountDAO()
    val transactionDAO = TransactionDAO()
    val accountService = AccountService(accountDAO)
    val correctionService = CorrectionService(accountDAO, transactionDAO)
    val transferService = TransferService(accountDAO, transactionDAO)

    routing {
        get("/balance") {
            val accountUUID = try {
                UUID.fromString(call.request.queryParameters["accountUUID"].toString())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid account UUID")
                return@get
            }

            when (val accountServiceResult = accountService.getAccountBalance(accountUUID)) {
                is AccountServiceResult.Failed.AccountDoesNotExist -> {
                    call.respond(HttpStatusCode.BadRequest, "Account does not exist")
                }
                is AccountServiceResult.Success.Balance -> {
                    call.respond("Account $accountUUID balance is ${accountServiceResult.balance}")
                }
            }
        }
        get("/credit") {
            val accountUUID = try {
                UUID.fromString(call.request.queryParameters["accountUUID"])
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid account UUID")
                        return@get
                    }
                    else -> throw e
                }
            }

            val amount = try {
                call.request.queryParameters["amount"]!!.toInt()
            } catch (e: Exception) {
                when (e) {
                    is NumberFormatException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid amount")
                        return@get
                    }
                    else -> throw e
                }
            }

            when (val correctionServiceResult = correctionService.creditMoney(accountUUID, amount)) {
                is CorrectionServiceResult.Failed.AccountDoesNotExist -> {
                    call.respond(HttpStatusCode.BadRequest, "Account does not exist")
                }
                is CorrectionServiceResult.Failed.NegativeDelta -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Can't make transaction with negative amount of money"
                    )
                }
                is CorrectionServiceResult.Success.Credited -> {
                    call.respond(HttpStatusCode.OK, "Successfully credited money")
                }
                else -> error("Unexpected $correctionServiceResult")
            }
        }
        get("/charge-off") {
            val accountUUID = try {
                UUID.fromString(call.request.queryParameters["accountUUID"])
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid account UUID")
                        return@get
                    }
                    else -> throw e
                }
            }

            val amount = try {
                call.request.queryParameters["amount"]!!.toInt()
            } catch (e: Exception) {
                when (e) {
                    is NumberFormatException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid amount")
                        return@get
                    }
                    else -> throw e
                }
            }

            when (val correctionServiceResult = correctionService.chargeOffMoney(accountUUID, amount)) {
                is CorrectionServiceResult.Failed.AccountDoesNotExist -> {
                    call.respond(HttpStatusCode.BadRequest, "Account does not exist")
                }
                is CorrectionServiceResult.Failed.NegativeDelta -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Can't make transaction with negative amount of money"
                    )
                }
                is CorrectionServiceResult.Failed.NotEnoughMoney -> {
                    call.respond(
                        HttpStatusCode.PreconditionFailed,
                        "Account does not have enough money"
                    )
                }
                is CorrectionServiceResult.Success.ChargedOff -> {
                    call.respond(HttpStatusCode.OK, "Successfully charged-off money")
                }
                else -> error("Unexpected $correctionServiceResult")
            }
        }
        get("/transfer") {
            val senderUUID = try {
                UUID.fromString(call.request.queryParameters["senderUUID"])
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid sender UUID")
                        return@get
                    }
                    else -> throw e
                }
            }

            val receiverUUID = try {
                UUID.fromString(call.request.queryParameters["receiverUUID"])
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid receiver UUID")
                        return@get
                    }
                    else -> throw e
                }
            }

            val amount = try {
                call.request.queryParameters["amount"]!!.toInt()
            } catch (e: Exception) {
                when (e) {
                    is NumberFormatException,
                    is NullPointerException -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid amount")
                        return@get
                    }
                    else -> throw e
                }
            }

            when (val transferServiceResult = transferService.makeTransfer(senderUUID, receiverUUID, amount)) {
                is TransferServiceResult.Failed.NegativeDelta -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Can't make transaction with negative amount of money"
                    )
                }
                is TransferServiceResult.Failed.NotEnoughMoney -> {
                    call.respond(
                        HttpStatusCode.PreconditionFailed,
                        "Sender does not have enough money"
                    )
                }
                is TransferServiceResult.Failed.SenderDoesNotExist -> {
                    call.respond(HttpStatusCode.BadRequest, "Sender does not exist")
                }
                is TransferServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, "Successfully transferred money")
                }
            }
        }
    }
}