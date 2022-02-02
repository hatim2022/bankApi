package com.dao;

import com.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public class TransactionDaoImpl implements TransactionDao{
    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public List<Transaction> getTransactionByUserId(int userId) throws DataAccessException {
        final String SELECT_ALL = "SELECT * FROM usertransaction WHERE userId = ?;";

        return jdbc.query(SELECT_ALL,new TransactionMapper(),userId);
    }

    @Override
    public List<Transaction> getTransactionByTransferFrom(int transactionfrom)throws DataAccessException {
        final String SELECT_ALL = "SELECT * FROM usertransaction WHERE transactionfrom = ?;";

        return jdbc.query(SELECT_ALL, new TransactionMapper(), transactionfrom);
    }

    @Override
    @Transactional
    public Transaction addTransaction(Transaction transaction) {

        final String ADD_TRANSACTION = "INSERT INTO usertransaction(userid, transactionamount, transactionfrom," +
                "transactionto, description) VALUES (?,?,?,?,?) RETURNING userid;";
        final String GET_TIMESTAMP = "SELECT transactionTimeStamp FROM userTransaction WHERE transactionId = ?;";

        int id = jdbc.queryForObject(ADD_TRANSACTION, Integer.class,
                transaction.getUserId(),
                transaction.getTransactionAmount(),
                transaction.getTransactionfrom(),
                transaction.getTransactionto(),
                transaction.getDescription()
        );

        Timestamp transactionTime = jdbc.queryForObject(GET_TIMESTAMP, Timestamp.class, id);

        transaction.setTransactionId(id);
        transaction.setTransactionTimeStamp(transactionTime.toLocalDateTime());

        return transaction;
    }

    public static final class TransactionMapper implements RowMapper<Transaction> {

        @Override
        public Transaction mapRow(ResultSet resultSet, int i) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(resultSet.getInt("transactionId"));
            transaction.setUserId(resultSet.getInt("userId"));
            transaction.setTransactionfrom(resultSet.getInt("transactionfrom"));
            transaction.setTransactionto(resultSet.getInt("transactionto"));
            transaction.setTransactionAmount(resultSet.getBigDecimal("transactionAmount"));
            transaction.setTransactionTimeStamp(resultSet.getTimestamp("transactionTimeStamp").toLocalDateTime());
            transaction.setDescription(resultSet.getString("description"));
            return transaction;
        }
    }



}
