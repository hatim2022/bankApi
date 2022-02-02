package com.controller;

import com.entities.Account;
import com.dto.AccountType;
import com.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CheckingAcctController extends BaseController{
    @Autowired
    BankService service;

    @PostMapping("/createCheckingAccount/{userId}")
    public ResponseEntity<Account> createCheckAcct(@PathVariable int userId) {
        Account checkingAccount = service.createCheckingAccount(userId);
        if(checkingAccount == null) {
            return new ResponseEntity("Checking account could not be created", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(checkingAccount);
    }

    @GetMapping("/checkingAccounts/{userId}")
    public ResponseEntity<List<Account>> getAllCheckingAccountsForUser(@PathVariable int userId) {
        List<Account> checkAccts = service.getAllCheckingAccountsForUser(userId);

        if(checkAccts.isEmpty()) {
            return new ResponseEntity("No checking accounts found for User #" + userId, HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(checkAccts);
        }

    }

    @GetMapping("/getCheckingAccount/{accountNumber}")
    public ResponseEntity<Account> getCheckingAccount(@PathVariable int accountNumber) {
        Account account = service.getCheckingAccount(accountNumber);

        if(account == null) {
            return new ResponseEntity("Checking account not found.", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(account);
        }
    }

    @PutMapping("/checkingAccount/withdraw")
    public ResponseEntity<Account> withdrawFromCheckingAccount(@RequestBody Account account) {
        account = service.withdrawFromAccount(account, AccountType.CHECKING);

        if(account == null) {
            return new ResponseEntity("Could not update account.", HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(account);
        }

    }

    @PutMapping("/checkingAccount/deposit")
    public ResponseEntity<Account> depositToCheckingAccount(@RequestBody Account account) {
        account = service.depositToAccount(account, AccountType.CHECKING);

        if(account == null) {
            return new ResponseEntity("Could not update account.", HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(account);
        }

    }

    @DeleteMapping("/checkingAccount/delete/{accountNumber}")
    public ResponseEntity<Account> deleteCheckingAccount(@PathVariable int accountNumber) {
        Account account = service.getCheckingAccount(accountNumber);

        if (account == null) {
            return new ResponseEntity("Account does not exist.", HttpStatus.NOT_FOUND);
        } else {
            account = service.deleteCheckingAccount(account);

            if (account == null) {
                return new ResponseEntity("Could not delete account.", HttpStatus.BAD_REQUEST);
            } else {
                return ResponseEntity.ok(account);
            }
        }
    }
}
