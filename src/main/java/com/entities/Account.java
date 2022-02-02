package com.entities;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private int userId;
    private int accountNumber;
    private BigDecimal balance;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return userId == account.userId && accountNumber == account.accountNumber && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, accountNumber, balance);
    }
}
