package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Bank.Money
import com.thecookiezen.AccountStatement
import java.time.LocalDate

import scala.collection.mutable.Map

case class Bank(accounts: Map[AccountId, Account])

object Bank {
  type Money       = BigDecimal
  type FindAccount = Bank => AccountId => Option[Account]
  type Deposit =
    Bank => FindAccount => Transaction => Either[AccountError, Unit]

  def apply(): Bank = new Bank(Map.empty)

  val createAccount: Bank => NewAccount => Account = bank =>
    newAccount => {
      val account = Account.from(newAccount)
      bank.accounts.put(account.id, account)
      account
    }

  val findAccount: FindAccount = bank => id => bank.accounts.get(id)

  val deposit: Deposit = bank =>
    findAccount =>
      transaction =>
        {
          findAccount(bank)(transaction.accountId)
            .map { acc =>
              acc.copy(transactionLog = acc.transactionLog :+ transaction)
            }
            .map(_ => ())
        }.toRight(AccountNotExist)

  val withdraw: Bank => Transaction => Unit                           = _ => _ => ()
  val accountStatement: Bank => AccountId => Option[AccountStatement] = _ => _ => None
}
