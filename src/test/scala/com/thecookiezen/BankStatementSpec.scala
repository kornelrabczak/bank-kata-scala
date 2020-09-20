package com.thecookiezen

import java.time.LocalDate
import java.util.UUID

import com.thecookiezen.Bank.{CheckAccountBalance, Money}
import com.thecookiezen.Account.AccountId
import com.thecookiezen.InsufficientBalance
import com.thecookiezen.Transaction.accountStatement
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

  private val checkIfAccountHasEnoughBalance                      = Account.accountService
  private val clock: Clock                                        = () => LocalDate.parse("2020-08-01")
  private val depositTransaction: (AccountId, Money) => Deposit   = Transaction.deposit(clock)
  private val withdrawTransaction: (AccountId, Money) => Withdraw = Transaction.withdraw(clock)
  private val bank                                                = Bank()
  private val getAccount                                          = Bank.getAccount(bank)
  private val addTransactionToAccount                             = Bank.addTransactionToAccount(bank)
  private val deposit                                             = Bank.deposit(bank)(getAccount)(addTransactionToAccount)
  private val withdraw                                            = Bank.withdraw(bank)(getAccount)(checkIfAccountHasEnoughBalance)(addTransactionToAccount)
  private val createAccount                                       = Bank.createAccount(bank)
  private val getAccountStatement                                 = Bank.accountStatement(getAccount)(accountStatement)

  "Bank" should "return error for deposit if account doesn't exist" in {
    deposit(depositTransaction("User1", 1000)) shouldBe Left(AccountNotExist)
  }

  it should "create a new account and deposit the money for a transaction" in {
    val account = createAccount(NewAccount("John", "Doe"))

    deposit(depositTransaction(account.id, 1000)) shouldBe Right(())
  }

  it should "add new transaction to a account audit log for a deposit" in {
    val account = createAccount(NewAccount("John", "Doe"))

    deposit(depositTransaction(account.id, 123))
    deposit(depositTransaction(account.id, 1000))

    bank.accounts.get(account.id).map(_.transactionLog.size) shouldBe Some(2)
  }

  it should "not withdraw from newly created account" in {
    val account = createAccount(NewAccount("John", "Doe"))

    withdraw(withdrawTransaction(account.id, 1000)) shouldBe Left(InsufficientBalance)
  }

  it should "not withdraw if there is not enough money on the account" in {
    val account = createAccount(NewAccount("John", "Doe"))

    deposit(depositTransaction(account.id, 123))
    withdraw(withdrawTransaction(account.id, 1000)) shouldBe Left(InsufficientBalance)
  }

  it should "withdraw the money from account" in {
    val account = createAccount(NewAccount("John", "Doe"))

    deposit(depositTransaction(account.id, 1000))
    withdraw(withdrawTransaction(account.id, 123))

    bank.accounts.get(account.id).map { acc =>
      acc.transactionLog.size shouldBe 2
      Account.checkBalance(Account.calculateBalance)(acc) shouldBe 877
    }.fold(fail())(_ => ())
  }

  it should "give a list of transaction for a account statement for a specified account" in {
    val account = createAccount(NewAccount("John", "Doe"))

    deposit(depositTransaction(account.id, 123))
    deposit(depositTransaction(account.id, 1000))

    getAccountStatement(account.id).map(_.transactions).get should contain inOrder (
      TransactionLog(Deposit(account.id, clock.now(), 123)),
      TransactionLog(Deposit(account.id, clock.now(), 1000))
    )
  }
}
