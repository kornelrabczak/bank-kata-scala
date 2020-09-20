package com.thecookiezen

import java.time.LocalDate

import com.thecookiezen.Bank.Money
import com.thecookiezen.Account.AccountId
import com.thecookiezen.InsufficientBalance

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AccountSpec extends AnyFlatSpec with Matchers {

  private val clock: Clock                                        = () => LocalDate.parse("2020-08-01")
  private val depositTransaction: (AccountId, Money) => Deposit   = Transaction.deposit(clock)
  private val withdrawTransaction: (AccountId, Money) => Withdraw = Transaction.withdraw(clock)

  "Account" should "calculate right balance from a transactions" in {
    Transaction.accountStatement(
      List(
        depositTransaction("someId", 123),
        withdrawTransaction("someId", 100),
        depositTransaction("someId", 7)
      )
    ).balance shouldBe 30
  }

  it should "check if there is enough balance for the withdraw" in {
    Account.checkIfEnoughBalance(100)(23) shouldBe Right(())
  }

  it should "return error if there is not enough balance for the withdraw" in {
    Account.checkIfEnoughBalance(23)(100) shouldBe Left(InsufficientBalance)
  }
}
