# Bank account kata

Bank kata

Test scenario:

```
Given a client makes a deposit of 1000 on 10-01-2012
And a deposit of 2000 on 13-01-2012
And a withdrawal of 500 on 14-01-2012
When she prints her bank statement
Then she would see
date || credit || debit || balance
14/01/2012 || || 500.00 || 2500.00
13/01/2012 || 2000.00 || || 3000.00
10/01/2012 || 1000.00 || || 1000.00
```

Requirements:

- Deposit and Withdrawal
- Transfer
- Account statement [](date, amount, balance)
- Statement printing
- Statement filters (just deposits, withdrawal, date)

Query:
- Bank statement ( list of all transactions and the balance after each one )

Commands:
- Deposit
- Withdraw
- Transaction

type AccountId = String
type Money = BigDecimal
type AccountStatement = Transaction + balance: Money 
type Transaction = (account: AccountId + date: Date + amount: Money    
type Transfer = (fromAccount, transaction) -> (fromAccount, transaction) -> Unit

deposit: Transaction -> Unit
withdraw: Transaction -> Unit
statement: AccountId -> foldLeft [AccountStatement]

type Terminal: List[AccountStatement] => String
printing: Terminal -> AccountId -> List[AccountStatement]
consolePrinting: AccountId -> List[AccountStatement] = printing(std out) _
