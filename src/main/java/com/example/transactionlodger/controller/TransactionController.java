package com.example.transactionlodger.controller;

import com.example.transactionlodger.dto.TransactionRequest;
import com.example.transactionlodger.entity.Transaction;
import com.example.transactionlodger.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public Transaction processTransaction(@RequestBody TransactionRequest request) {
        return transactionService.processTransaction(request);
    }
}