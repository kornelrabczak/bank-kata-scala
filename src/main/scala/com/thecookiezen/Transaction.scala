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

case class AccountStatement(transactions: List[TransactionLog], balance: Money)
case class TransactionLog(transaction: Transaction, balance: Money)

object Transaction {
  type GetStatement = List[Transaction] => AccountStatement

  val withdraw: Clock => (AccountId, Money) => Withdraw = clock => (id, money) => Withdraw(id, clock.now(), money)
  val deposit: Clock => (AccountId, Money) => Deposit   = clock => (id, money) => Deposit(id, clock.now(), money)

  val accountStatement: GetStatement = _.foldLeft(AccountStatement(List.empty, BigDecimal(0))) { (acc, transaction) =>
    val currentBalance = transaction match {
      case Deposit(_, _, amount)  => acc.balance + amount
      case Withdraw(_, _, amount) => acc.balance - amount
    }
    acc.copy(transactions = acc.transactions :+ TransactionLog(transaction, currentBalance), currentBalance)
  }
}
