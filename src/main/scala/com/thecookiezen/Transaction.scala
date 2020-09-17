package com.thecookiezen

import com.thecookiezen.Account.AccountId
import com.thecookiezen.Bank.Money
import java.time.LocalDate

case class Transaction(accountId: AccountId, date: LocalDate, amount: Money)
case class AccountStatement(transactions: List[TransactionLog])
case class TransactionLog(transaction: Transaction, balance: Money = 0)

object Transaction {
  type CreateTransationLog = Account => AccountStatement

  val deposit: Clock => (AccountId, Money) => Transaction = clock => (id, amount) => Transaction(id, clock.now(), amount)

  val accountStatement: CreateTransationLog = account =>
    AccountStatement(account.transactionLog.map(transaction => TransactionLog(transaction = transaction)))
}
