package com.thecookiezen

import com.thecookiezen.Account.{AccountId, CheckIfAccountHasEnoughBalance}
import com.thecookiezen.Bank.Money
import com.thecookiezen.AccountStatement
import com.thecookiezen.Transaction.CreateTransationLog
import java.time.LocalDate

import scala.collection.mutable.Map

case class Bank(accounts: Map[AccountId, Account])

object Bank {
  type Money                   = BigDecimal
  type GetAccount              = AccountId => Option[Account]
  type DepositOperation        = Bank => GetAccount => AddTransactionToAccount => Transaction => Either[AccountError, Unit]
  type CheckAccountBalance     = Account => Withdraw => Either[AccountError, Account]
  type WithdrawOperation       =
    Bank => GetAccount => CheckAccountBalance => AddTransactionToAccount => Withdraw => Either[AccountError, Unit]
  type AddTransactionToAccount = (Account, Transaction) => Unit

  def apply(): Bank = new Bank(Map.empty)

  val getAccount: Bank => GetAccount = bank => accId => bank.accounts.get(accId)

  val addTransactionToAccount: Bank => AddTransactionToAccount = bank =>
    (acc, transaction) => {
      val updatedAccount = acc.copy(transactionLog = acc.transactionLog :+ transaction)
      bank.accounts.put(acc.id, updatedAccount)
      ()
    }

  val createAccount: Bank => NewAccount => Account = bank =>
    newAccount => {
      val account = Account.from(newAccount)
      bank.accounts.put(account.id, account)
      account
    }

  val deposit: DepositOperation = bank =>
    getAccount =>
      addTransaction =>
        transaction => {
          getAccount(transaction.accountId)
            .map(acc => addTransaction(acc, transaction))
            .toRight(AccountNotExist)
        }

  val withdraw: WithdrawOperation = bank =>
    getAccount =>
      checkBalance =>
        addTransaction =>
          transaction => {
            getAccount(transaction.accountId)
              .toRight(AccountNotExist)
              .flatMap(acc => checkBalance(acc)(transaction))
              .map(acc => addTransaction(acc, transaction))
          }

  val accountStatement: GetAccount => CreateTransationLog => AccountId => Option[AccountStatement] = getAccount =>
    getStatement =>
      accountId => {
        getAccount(accountId)
          .map(getStatement)
      }
}
