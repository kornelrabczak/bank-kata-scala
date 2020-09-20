package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Bank.Money
import java.time.LocalDate

sealed abstract class Transaction {
  def accountId: AccountId
  def date: LocalDate
  def amount: Money
}
case class Deposit(accountId: AccountId, date: LocalDate, amount: Money)  extends Transaction
case class Withdraw(accountId: AccountId, date: LocalDate, amount: Money) extends Transaction

case class AccountStatement(transactions: List[TransactionLog])
case class TransactionLog(transaction: Transaction, balance: Money = 0)

object Transaction {
  type CreateTransationLog = Account => AccountStatement

  val withdraw: Clock => (AccountId, Money) => Withdraw = clock => (id, money) => Withdraw(id, clock.now(), money)
  val deposit: Clock => (AccountId, Money) => Deposit   = clock => (id, money) => Deposit(id, clock.now(), money)

  val accountStatement: CreateTransationLog = account =>
    AccountStatement(account.transactionLog.map(transaction => TransactionLog(transaction = transaction)))
}
