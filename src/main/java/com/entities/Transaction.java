package com.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private int userId;
    private BigDecimal transactionAmount;
    private int transactionfrom;
    private int transactionto;
    private LocalDateTime transactionTimeStamp;

    public LocalDateTime getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionTimeStamp(LocalDateTime transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }

    private String description;


    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public int getTransactionfrom() {
        return transactionfrom;
    }

    public void setTransactionfrom(int transactionfrom) {
        this.transactionfrom = transactionfrom;
    }

    public int getTransactionto() {
        return transactionto;
    }

    public void setTransactionto(int transactionto) {
        this.transactionto = transactionto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", transactionAmount=" + transactionAmount +
                ", transactionfrom=" + transactionfrom +
                ", transactionto=" + transactionto +
                '}';
    }
}
