package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Bank.Money
import java.time.LocalDate

case class Transaction(accountId: AccountId, date: LocalDate, amount: Money)
case class AccountStatement(Transactions: List[TransactionLog])
case class TransactionLog(Transaction: Transaction, balance: Money)

object Transaction {
  val deposit: Clock => (AccountId, Money) => Transaction = clock =>
    (id, amount) => Transaction(id, clock.now(), amount)
}
