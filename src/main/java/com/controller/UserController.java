package com.controller;

import com.dto.LoginParam;
import com.entities.AccountUser;
import com.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends BaseController {
    @Autowired
    BankService service;

    @PostMapping("/getUser")
    public ResponseEntity<AccountUser> login(@RequestBody LoginParam param) {
        AccountUser user = service.getUserByEmail(param.getEmail());
        if (user == null) {
            return new ResponseEntity("Cannot find user.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/createUser")
    public ResponseEntity<AccountUser> createUser(@RequestBody AccountUser newUser) {
        AccountUser user = service.createUser(newUser);
        if (user == null) {
            return new ResponseEntity("Fail to create new account", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(user);
    }
}
