package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Transaction.GetStatement
import com.thecookiezen.Bank.{GetAccount, Money}
import java.util.UUID

case class Account(
    id: AccountId,
    firstName: String,
    secondName: String,
    transactionLog: List[Transaction]
)
case class NewAccount(firstName: String, secondName: String)

sealed abstract class AccountError
case object AccountNotExist     extends AccountError
case object InsufficientBalance extends AccountError

object Account {
  type AccountId                      = String
  type CheckBalance                   = GetStatement => Account => Money
  type CheckIfEnoughBalance           = Money => Money => Either[AccountError, Unit]
  type CheckIfAccountHasEnoughBalance =
    GetStatement => CheckBalance => CheckIfEnoughBalance => Account => Withdraw => Either[AccountError, Account]

  val from: NewAccount => Account = newAccount =>
    Account(
      id = UUID.randomUUID().toString,
      firstName = newAccount.firstName,
      secondName = newAccount.secondName,
      transactionLog = List.empty
    )

  val checkBalance: CheckBalance = calculateBalance => account => calculateBalance(account.transactionLog).balance

  val checkIfEnoughBalance: CheckIfEnoughBalance = balance =>
    transfer => Either.cond(balance - transfer > 0, (), InsufficientBalance)

  val checkIfAccountHasEnoughBalance: CheckIfAccountHasEnoughBalance = calculate =>
    balance => isEnough => account => withdraw => isEnough(balance(calculate)(account))(withdraw.amount).map(_ => account)

  val accountService: Account => Withdraw => Either[AccountError, Account] =
    checkIfAccountHasEnoughBalance(Transaction.accountStatement)(checkBalance)(checkIfEnoughBalance)
}
