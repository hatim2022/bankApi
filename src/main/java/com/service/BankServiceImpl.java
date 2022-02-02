package com.service;

import com.dao.TransactionDao;
import com.dao.AccountDao;
import com.dao.UserDao;
import com.entities.Account;
import com.dto.AccountType;
import com.entities.AccountUser;
import com.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Repository
public class BankServiceImpl implements BankService{
    @Autowired
    UserDao userDao;

    @Autowired
    TransactionDao transactionDao;
  
    @Autowired
    @Qualifier("checkingAccount")
    AccountDao checkingAccountDao;
  
    @Autowired
    @Qualifier("savingAccountDaoImpl")
    AccountDao savingAccountDao;

    @Override
    public AccountUser getUserByEmail(String email) {
        try {
            AccountUser user = userDao.getUser(email);
            user.setPassword("HIDDEN");
            return user;
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public AccountUser createUser(AccountUser user) {
        try {
            user.setPassword(encryptPassword(user.getPassword()));
            AccountUser newUser = userDao.createUser(user);
            newUser.setPassword("HIDDEN");
            createCheckingAccount(newUser.getUserId());
            createSavingAccount(newUser.getUserId());
            return newUser;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Transaction> getAllTransactionsByUserId(int userId) {
        return transactionDao.getTransactionByUserId(userId);
    }

    @Override
    public List<Transaction> getAllTransactionsByTransferFrom(int transactionfrom) {
        return transactionDao.getTransactionByTransferFrom(transactionfrom);
    }

    @Override
    public Transaction transferMoney(Transaction transaction) {
        try {
            BigDecimal amount = transaction.getTransactionAmount();

            Account checkingAccountFrom = checkIfCheckingAccountExist(transaction.getTransactionfrom(), amount);
            Account savingAccountFrom = checkIfSavingAccountExist(transaction.getTransactionfrom(), amount);
            Account checkingAccountTo = checkIfCheckingAccountExist(transaction.getTransactionto(), amount);
            Account savingAccountTo = checkIfSavingAccountExist(transaction.getTransactionto(), amount);

            if (checkingAccountFrom != null && checkingAccountTo != null) {
                checkingToCheckingAccount(checkingAccountFrom, checkingAccountTo, amount);
                return saveTransaction(transaction);
            } else if (savingAccountFrom != null && checkingAccountTo != null) {
                savingToCheckingAccount(savingAccountFrom, checkingAccountTo, amount);
                return saveTransaction(transaction);
            } else if (checkingAccountFrom != null && savingAccountTo != null) {
                checkingToSavingAccount(checkingAccountFrom, savingAccountTo, amount);
                return saveTransaction(transaction);
            } else if (savingAccountFrom != null && savingAccountTo != null) {
                savingToSavingAccount(savingAccountFrom, savingAccountTo, amount);
                return saveTransaction(transaction);
            } else {
                return null;
            }
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<Account> getAllSavingAccountForUser(int userId) {
         try {
            return savingAccountDao.getAccountsForUser(userId);
         }catch (DataAccessException ex){
             return null;
         }
    }

    @Override
    public Account getSavingAccountForUser(int accountNumber) {
        try {
            return savingAccountDao.getAccount(accountNumber);
        }catch (DataAccessException ex){
            return null;
        }
    }

    @Override
    public Account createSavingAccount(int userId) {

        try {
            Account account = new Account();
            int num = generateAccountNumber();
            account.setAccountNumber(num);
            account.setUserId(userId);
            account.setBalance(new BigDecimal("0.00"));
            return savingAccountDao.createAccount(account);
        }catch (DataAccessException  ex){
            return null;
        }
    }

    @Override
    public Account deleteSavingAccount(Account account) {
        try {
            return savingAccountDao.deleteAccount(account);
        }catch (DataAccessException ex){
            return null;
        }
    }

    @Override
    public Account createCheckingAccount(int userId) {
        try {
            Account newAccount = new Account();
            newAccount.setUserId(userId);
            newAccount.setAccountNumber(generateAccountNumber());
            newAccount.setBalance(new BigDecimal("0.00"));
            return checkingAccountDao.createAccount(newAccount);
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Override
    public Account getCheckingAccount(int accountNumber) {
        try {
            return checkingAccountDao.getAccount(accountNumber);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Account> getAllCheckingAccountsForUser(int userId) {
        try {
            return checkingAccountDao.getAccountsForUser(userId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Account withdrawFromAccount(Account accountFromWithdrawal, AccountType accType) {
        try {
            BigDecimal withdrawalAmount = accountFromWithdrawal.getBalance();

            switch(accType) {
                case CHECKING:

                    Account accountFromDao = checkingAccountDao.getAccount(accountFromWithdrawal.getAccountNumber());

                    if (!checkIfNegativeAmount(withdrawalAmount) && accountFromDao.getBalance().compareTo(BigDecimal.ZERO) >= 0) {
                        accountFromDao.setBalance(accountFromDao.getBalance().subtract(withdrawalAmount));
                        if (accountFromDao.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                            accountFromDao.setBalance(accountFromDao.getBalance().subtract(new BigDecimal("50")));
                        }
                        accountFromDao = checkingAccountDao.updateAccount(accountFromDao);
                        Transaction transaction = createTransaction(accountFromDao, "withdraw", withdrawalAmount);
                        transactionDao.addTransaction(transaction);
                        return accountFromDao;
                    } else { return null; }

                case SAVING:
                    Account savingAccountFromDao = savingAccountDao.getAccount(accountFromWithdrawal.getAccountNumber());
                    if (checkTransactionAmount(savingAccountFromDao, withdrawalAmount)) {
                        savingAccountFromDao.setBalance(savingAccountFromDao.getBalance().subtract(withdrawalAmount));
                        savingAccountFromDao = savingAccountDao.updateAccount(savingAccountFromDao);
                        Transaction transaction = createTransaction(savingAccountFromDao, "withdraw", withdrawalAmount);
                        transactionDao.addTransaction(transaction);
                        return savingAccountFromDao;
                    } else { return null; }

                default:
                    return null;
            }

        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public Account depositToAccount(Account accountFromDeposit, AccountType accType) {
        try {
            BigDecimal depositAmount = accountFromDeposit.getBalance();

            switch(accType) {
                case CHECKING:

                    Account accountFromDao = checkingAccountDao.getAccount(accountFromDeposit.getAccountNumber());
                    if (!checkIfNegativeAmount(depositAmount)) {
                        accountFromDao.setBalance(accountFromDao.getBalance().add(depositAmount));
                        accountFromDao = checkingAccountDao.updateAccount(accountFromDao);
                        Transaction transaction = createTransaction(accountFromDao, "deposit", depositAmount);
                        transactionDao.addTransaction(transaction);
                        return accountFromDao;
                    } else { return null; }

                case SAVING:
                    Account savingAccountFromDao = savingAccountDao.getAccount(accountFromDeposit.getAccountNumber());
                    if (!checkIfNegativeAmount(depositAmount)) {
                        savingAccountFromDao.setBalance(savingAccountFromDao.getBalance().add(depositAmount));
                        savingAccountFromDao = savingAccountDao.updateAccount(savingAccountFromDao);
                        Transaction transaction = createTransaction(savingAccountFromDao, "deposit", depositAmount);
                        transactionDao.addTransaction(transaction);
                        return savingAccountFromDao;
                    } else { return null; }
                
                default:
                    return null;
            }
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public Account deleteCheckingAccount(Account account) {
        try {
            return checkingAccountDao.deleteAccount(account);
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    private void checkingToCheckingAccount(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        checkingAccountDao.updateAccount(from);
        checkingAccountDao.updateAccount(to);
    }

    private void checkingToSavingAccount(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        checkingAccountDao.updateAccount(from);
        savingAccountDao.updateAccount(to);
    }

    private void savingToSavingAccount(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        savingAccountDao.updateAccount(from);
        savingAccountDao.updateAccount(to);
    }

    private void savingToCheckingAccount(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        savingAccountDao.updateAccount(from);
        checkingAccountDao.updateAccount(to);
    }

    private Transaction saveTransaction(Transaction transaction) {
        Transaction newTransaction = createTransaction(transaction, "transfer");
        return transactionDao.addTransaction(newTransaction);
    }

    private Account checkIfCheckingAccountExist(int accountNumber, BigDecimal transactionAmount) {
        try {
            Account checkingAcct = checkingAccountDao.getAccount(accountNumber);
            if (checkTransactionAmount(checkingAcct, transactionAmount)) {
                return checkingAcct;
            } else {
                return null;
            }
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    private Account checkIfSavingAccountExist(int accountNumber, BigDecimal transactionAmount) {
        try {
            Account savingAcct = savingAccountDao.getAccount(accountNumber);
            if (checkTransactionAmount(savingAcct, transactionAmount)) {
                return savingAcct;
            } else {
                return null;
            }
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    private Transaction createTransaction(Account account, String description, BigDecimal amount) {
        Transaction newTransaction = new Transaction();
        newTransaction.setUserId(account.getUserId());
        newTransaction.setTransactionAmount(amount);
        newTransaction.setTransactionfrom(account.getAccountNumber());
        newTransaction.setTransactionto(account.getAccountNumber());
        newTransaction.setDescription(description);
        return newTransaction;
    }

    private Transaction createTransaction(Transaction transaction, String description) {
        Transaction newTransaction = new Transaction();
        newTransaction.setUserId(transaction.getUserId());
        newTransaction.setTransactionAmount(transaction.getTransactionAmount());
        newTransaction.setTransactionfrom(transaction.getTransactionfrom());
        newTransaction.setTransactionto(transaction.getTransactionto());
        newTransaction.setDescription(description);
        return newTransaction;
    }

    private int generateAccountNumber() {
        Random rand = new Random();

        int generatedAccountNumber = rand.nextInt(899999) + 100000;
        while(checkingAccountDao.checkAccountNumber(generatedAccountNumber)
                || savingAccountDao.checkAccountNumber(generatedAccountNumber) ) {
            generatedAccountNumber = rand.nextInt(899999) + 100000;
        }

        return generatedAccountNumber;
    }

    private boolean checkTransactionAmount(Account accountFromDao, BigDecimal transactionAmount) {
        if(!checkIfNegativeAmount(transactionAmount) && accountFromDao.getBalance().compareTo(transactionAmount) >= 0) {
            return true; //GOOD
        } else {
            return false; //NOT GOOD
        }
    }

    private boolean checkIfNegativeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return true; //NOT GOOD
        } else {
            return false; //GOOD
        }
    }

    private String encryptPassword(String password) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(password.getBytes());
    }

    private String decryptPassword(String encryptedPassword) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(encryptedPassword);
        return new String(bytes);
    }
}
