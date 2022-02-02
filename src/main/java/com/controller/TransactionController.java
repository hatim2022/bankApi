package com.controller;

import com.entities.Transaction;
import com.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class TransactionController extends BaseController {

    @Autowired
    BankService service;

    @GetMapping("/gettransactions/{userId}") //http://localhost:8080/api/gettransactions/2/
    public ResponseEntity<List<Transaction>> getTransactionByUserId(@PathVariable int userId){

        List<Transaction> transactions = service.getAllTransactionsByUserId(userId);

        if(transactions.isEmpty()) {
            return new ResponseEntity("Transaction not found.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/gettransactionfrom/{transactionfrom}") //http://localhost:8080/api/gettransactionfrom/3456788
    public ResponseEntity<List<Transaction>> getTransactionByTransferFrom(@PathVariable int transactionfrom){

        List<Transaction> transactions = service.getAllTransactionsByTransferFrom(transactionfrom);
        if(transactions.isEmpty()) {
            return new ResponseEntity("Transaction not found.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@RequestBody Transaction transaction) {
        Transaction newTransaction = service.transferMoney(transaction);
        if (newTransaction == null) {
            return new ResponseEntity("Transaction failed.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(newTransaction);
    }

}
