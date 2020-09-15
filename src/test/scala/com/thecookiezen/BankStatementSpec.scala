package com.thecookiezen

import java.time.LocalDate
import java.util.UUID

import com.thecookiezen.Bank.Money
import com.thecookiezen.Account.AccountId
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
  private val transaction: (AccountId, Money) => Transaction =
    Transaction.newTransaction(clock)

  "Bank" should "return error for deposit if account doesn't exist" in {
    val bank = Bank()
    val deposit = Bank.deposit(bank)(Bank.findAccount)

    deposit(transaction("User1", 1000)) shouldBe Left(AccountNotExist)
  }
}
