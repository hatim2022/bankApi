package com.dao;

import com.entities.Account;

import java.util.List;

public interface AccountDao {
    Account createAccount(Account account);
    List<Account> getAccountsForUser(int userId);
    Account getAccount(int accountNumber);
    Account updateAccount(Account account);
    Account deleteAccount(Account account);
    boolean checkAccountNumber(int accountNumber);
}
