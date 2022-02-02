package com.dao;

import com.entities.AccountUser;

public interface UserDao {
    AccountUser getUser(String email);
    AccountUser createUser(AccountUser user);
}
