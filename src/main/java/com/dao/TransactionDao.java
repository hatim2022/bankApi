package com.dao;

import com.entities.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> getTransactionByUserId(int userId);
    List<Transaction> getTransactionByTransferFrom(int transferfrom);
    Transaction addTransaction(Transaction transaction);
}
