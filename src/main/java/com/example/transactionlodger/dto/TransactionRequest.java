package com.example.transactionlodger.dto;

import com.example.transactionlodger.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequest {

    private UUID transactionId;
    private UUID userId;
    private BigDecimal amount;
    private TransactionType type;

    public TransactionRequest() {
    }

    public TransactionRequest(UUID transactionId, UUID userId,
                              BigDecimal amount, TransactionType type) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}