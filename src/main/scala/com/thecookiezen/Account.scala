package com.thecookiezen

import com.thecookiezen.Account.AccountId
import java.util.UUID

case class Account(
    id: AccountId,
    firstName: String,
    secondName: String,
    transactionLog: List[Transaction]
)
case class NewAccount(firstName: String, secondName: String)

sealed abstract class AccountError
case object AccountNotExist extends AccountError

object Account {
  type AccountId = String

  val from: NewAccount => Account = newAccount =>
    Account(
      id = UUID.randomUUID().toString,
      firstName = newAccount.firstName,
      secondName = newAccount.secondName,
      transactionLog = List.empty
    )
}
