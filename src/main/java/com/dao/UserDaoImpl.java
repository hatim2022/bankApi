package com.dao;

import com.entities.AccountUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserDaoImpl implements UserDao{
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public AccountUser getUser(String email) throws DataAccessException {
        final String GET_USER = "SELECT * FROM accountUser WHERE email = ?;";
        return jdbc.queryForObject(GET_USER, new UserMapper(), email);
    }

    @Override
    public AccountUser createUser(AccountUser user) throws DataAccessException{
        try {
            final String INSERT_NEW_USER = "INSERT INTO accountUser (firstName, lastName, email, phoneNumber, address, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING userId;";

            int id = jdbc.queryForObject(INSERT_NEW_USER, Integer.class,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getPassword()
            );

            user.setUserId(id);

            return user;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static final class UserMapper implements RowMapper<AccountUser> {

        @Override
        public AccountUser mapRow(ResultSet rs, int index) throws SQLException {
            AccountUser user = new AccountUser();
            user.setUserId(rs.getInt("userId"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phoneNumber"));
            user.setAddress(rs.getString("address"));
            user.setPassword(rs.getString("password"));

            return user;
        }
    }
}
