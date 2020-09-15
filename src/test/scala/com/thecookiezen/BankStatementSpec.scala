package com.thecookiezen

import java.time.LocalDate
import java.util.UUID

import com.thecookiezen.Bank.{AccountId, Money}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.Map

class BankStatementSpec extends AnyFlatSpec with Matchers {
  """
    |Given a client makes a deposit of 1000 on 10-01-2012
    |And a deposit of 2000 on 13-01-2012
    |And a withdrawal of 500 on 14-01-2012
    |When she prints her bank statement
    |Then she would see
    |date || credit || debit || balance
    |14/01/2012 || || 500.00 || 2500.00
    |13/01/2012 || 2000.00 || || 3000.00
    |10/01/2012 || 1000.00 || || 1000.00
    |
    |""".stripMargin

  private val clock: Clock = () => LocalDate.now()
  private val transaction: (AccountId, Money) => Transaction = Transaction.newTransaction(clock)

  "Bank" should "return error for deposit if account doesn't exist" in {
    val bank = Bank()
    val deposit = Bank.deposit(bank)(Bank.findAccount)

    deposit(transaction("User1", 1000)) shouldBe Left(AccountNotExist)
  }
}


case class Transaction(accountId: AccountId, date: LocalDate, amount: Money)
case class Account(id: AccountId, firstName: String, secondName: String, transactionLog: List[Transaction])
case class NewAccount(firstName: String, secondName: String)
case class Bank(accounts: Map[AccountId, Account])
case class AccountStatement()

trait Clock {
  def now(): LocalDate
}

object Account {
  val from: NewAccount => Account = newAccount => Account(
    id = UUID.randomUUID().toString,
    firstName = newAccount.firstName,
    secondName = newAccount.secondName,
    transactionLog = List.empty
  )
}

object Transaction {
  val newTransaction: Clock => (AccountId, Money) => Transaction = clock => (id, amount) => Transaction(id, clock.now(), amount)
}

sealed abstract class AccountError
case object AccountNotExist extends AccountError 

object Bank {
  type AccountId = String
  type Money = BigDecimal
  type FindAccount = Bank => AccountId => Option[Account]
  type Deposit = Bank => FindAccount => Transaction => Either[AccountError, Unit]

  val createAccount: Bank => NewAccount => Account = bank => newAccount => {
    val account = Account.from(newAccount)
    bank.accounts.put(account.id, account)
    account
  }

  val findAccount: FindAccount = bank => id => bank.accounts.get(id)

  val deposit: Deposit = bank => findAccount => transaction => {
    findAccount(bank)(transaction.accountId).map { acc =>
      acc.copy(transactionLog = acc.transactionLog :+ transaction)
    }.map(_ => ())
  }.toRight(AccountNotExist)

  val withdraw: Bank => Transaction => Unit = _ => _ => ()
  val accountStatement: Bank => AccountId => AccountStatement = _ => _ => AccountStatement()

  def apply(): Bank = new Bank(Map.empty)
}