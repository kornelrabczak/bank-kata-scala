package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Bank.Money
import com.thecookiezen.AccountStatement
import com.thecookiezen.Transaction.CreateTransationLog
import java.time.LocalDate

import scala.collection.mutable.Map

case class Bank(accounts: Map[AccountId, Account])

object Bank {
  type Money       = BigDecimal
  type FindAccount = Bank => AccountId => Option[Account]
  type GetAccount  = AccountId => Option[Account]
  type Deposit     = Bank => GetAccount => Transaction => Either[AccountError, Unit]

  def apply(): Bank = new Bank(Map.empty)

  val getAccount: Bank => GetAccount = bank => accId => bank.accounts.get(accId)

  val createAccount: Bank => NewAccount => Account = bank =>
    newAccount => {
      val account = Account.from(newAccount)
      bank.accounts.put(account.id, account)
      account
    }

  val deposit: Deposit = bank =>
    getAccount =>
      transaction => {
        getAccount(transaction.accountId)
          .map { acc =>
            val updatedAccount = acc.copy(transactionLog = acc.transactionLog :+ transaction)
            bank.accounts.put(acc.id, updatedAccount)
          }
          .map(_ => ())
          .toRight(AccountNotExist)
      }

  val withdraw: Bank => Transaction => Unit = _ => _ => ()

  val accountStatement: GetAccount => CreateTransationLog => AccountId => Option[AccountStatement] = getAccount =>
    getStatement =>
      accountId => {
        getAccount(accountId)
          .map(getStatement)
      }
}
