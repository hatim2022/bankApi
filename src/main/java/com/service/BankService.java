package com.service;

import com.entities.Account;
import com.dto.AccountType;
import com.entities.AccountUser;
import com.entities.Transaction;

import java.util.List;

public interface BankService {
    AccountUser getUserByEmail(String email);
    AccountUser createUser(AccountUser user);
  
    Account createCheckingAccount(int userId);
    Account getCheckingAccount(int accountNumber);
    List<Account> getAllCheckingAccountsForUser(int userId);
    Account deleteCheckingAccount(Account account);
  
    Account withdrawFromAccount(Account accountFromWithdrawal, AccountType accType);
    Account depositToAccount(Account accountFromDeposit, AccountType accType);
  
    List<Account> getAllSavingAccountForUser(int userId);
    Account getSavingAccountForUser(int accountNumber);
    Account createSavingAccount(int userId);
    Account deleteSavingAccount(Account account);
  
    List<Transaction> getAllTransactionsByUserId(int userId);
    List<Transaction> getAllTransactionsByTransferFrom(int transactionfrom);

    Transaction transferMoney(Transaction transaction);
}
