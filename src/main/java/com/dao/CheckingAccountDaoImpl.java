package com.dao;

import com.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component("checkingAccount")
public class CheckingAccountDaoImpl implements AccountDao{

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Account createAccount(Account account) {
        final String INSERT_NEW_ACCOUNT = "INSERT INTO checkingAccount (accountNumber, userId) "
                + "VALUES (?, ?);";

        jdbc.update(INSERT_NEW_ACCOUNT,
                account.getAccountNumber(),
                account.getUserId()
        );

        return account;
    }

    @Override
    public List<Account> getAccountsForUser(int userId) throws DataAccessException {
        final String GET_ACCOUNTS_FOR_USER = "SELECT * from checkingAccount WHERE userId = ?;";

        return jdbc.query(GET_ACCOUNTS_FOR_USER, new CheckingAccountMapper(), userId);
    }

    @Override
    public Account getAccount(int accountNumber) throws DataAccessException {
        final String GET_ACCOUNT = "SELECT * from checkingAccount WHERE accountNumber = ?;";

        return jdbc.queryForObject(GET_ACCOUNT, new CheckingAccountMapper(), accountNumber);
    }

    @Override
    public Account updateAccount(Account account) {
        final String UPDATE_ACCOUNT = "UPDATE checkingAccount SET balance = ? WHERE accountNumber = ? RETURNING userId;";

        int id = jdbc.queryForObject(UPDATE_ACCOUNT, Integer.class, account.getBalance(), account.getAccountNumber());

        account.setUserId(id);

        return account;
    }

    @Override
    public Account deleteAccount(Account account) {
        final String DELETE_ACCOUNT = "DELETE FROM checkingAccount WHERE accountNumber = ?;";

        jdbc.update(DELETE_ACCOUNT, account.getAccountNumber());

        return account;
    }

    @Override
    public boolean checkAccountNumber(int accountNumber) {

        final String SELECT_ACCOUNT_NUMBER = "SELECT accountNumber FROM checkingAccount " +
                "WHERE accountNumber = ?;";

        List<Integer> accountNums = jdbc.queryForList(SELECT_ACCOUNT_NUMBER, Integer.class, accountNumber);

        if(accountNums.contains(accountNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public static final class CheckingAccountMapper implements RowMapper<Account> {

        @Override
        public Account mapRow(ResultSet rs, int index) throws SQLException {
            Account account = new Account();
            account.setAccountNumber(rs.getInt("accountnumber"));
            account.setUserId(rs.getInt("userid"));
            account.setBalance(rs.getBigDecimal("balance"));

            return account;
        }
    }
}
