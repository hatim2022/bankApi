package com.dao;

import com.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component("savingAccountDaoImpl")
public class SavingAccountDaoImpl implements AccountDao{

    @Autowired
    JdbcTemplate jdbc;
    @Override
    public Account createAccount(Account account) {

        try {
            final String INSERT_NEW_SAVING_ACCOUNT = "INSERT INTO savingaccount (accountnumber, userid) " +
                    "VALUES (?, ?);";
           jdbc.update(INSERT_NEW_SAVING_ACCOUNT, account.getAccountNumber(), account.getUserId());
           return account;
        }catch (NullPointerException | DataAccessException ex){
            return null;
        }
    }

    @Override
    public List<Account> getAccountsForUser(int userId) {
        final String SELECT_SAVING_ACCOUNT = "SELECT * FROM savingaccount WHERE userid = ?;";
        try {
            return jdbc.query(SELECT_SAVING_ACCOUNT, new accountMapper(), userId);
        }catch (DataAccessException ex){
            return null;
         }
    }

    @Override
    public Account getAccount(int accountNumber) {

        final String SELECT_Investment = "SELECT * FROM savingaccount WHERE accountnumber = ?;";
        try {
            return jdbc.queryForObject(SELECT_Investment, new accountMapper(), accountNumber);
        }catch (DataAccessException ex){
            return null;
        }
    }

    @Override
    public Account updateAccount(Account account) {

        try {
            final String UPDATE_SAVING_ACCOUNT = "UPDATE savingaccount SET balance = ? WHERE accountnumber = ? RETURNING userId;" ;
            int id = jdbc.queryForObject(UPDATE_SAVING_ACCOUNT, Integer.class, account.getBalance(), account.getAccountNumber());
            account.setUserId(id);
            return account;
        }catch (NullPointerException | DataAccessException ex){
            return null;
        }
    }

    @Override
    public Account deleteAccount(Account account) {

        try {
            final String DELETE_SAVING_ACCOUNT = "DELETE FROM savingaccount WHERE accountnumber = ?;";
            jdbc.update(DELETE_SAVING_ACCOUNT, account.getAccountNumber());
            return account;
        }catch (NullPointerException | DataAccessException ex){
            return null;
        }
    }

    @Override
    public boolean checkAccountNumber(int accountNumber) {

        final String SELECT_ACCOUNT_NUMBER = "SELECT accountNumber FROM checkingAccount WHERE accountNumber=? ;";

        List<Integer> accountNums = jdbc.queryForList(SELECT_ACCOUNT_NUMBER, Integer.class, accountNumber);

        if(accountNums.contains(accountNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public static final class accountMapper implements RowMapper<Account> {
        public Account mapRow(ResultSet resultSet, int i) throws SQLException {
            Account account = new Account();
            account.setAccountNumber(resultSet.getInt("accountnumber"));
            account.setUserId(resultSet.getInt("userid"));
            account.setBalance(resultSet.getBigDecimal("balance"));
            return account;
        }
    }
}
