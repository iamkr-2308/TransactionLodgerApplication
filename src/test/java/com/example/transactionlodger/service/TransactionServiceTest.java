package com.example.transactionlodger.service;

import com.example.transactionlodger.dto.TransactionRequest;
import com.example.transactionlodger.entity.Transaction;
import com.example.transactionlodger.entity.TransactionType;
import com.example.transactionlodger.entity.Wallet;
import com.example.transactionlodger.repository.TransactionRepository;
import com.example.transactionlodger.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final UUID testUserId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {

        transactionRepository.deleteAll();
        walletRepository.deleteAll();

        Wallet wallet = new Wallet(
                testUserId,
                new BigDecimal("1000.00")
        );

        walletRepository.save(wallet);
    }

    @Test
    @DisplayName("Processes a single valid debit transaction successfully.")
    void processesSingleValidDebitTransactionSuccessfully() {

        System.out.println();
        System.out.println("TEST: Processes a single valid debit transaction successfully.");

        UUID transactionId =
                UUID.fromString("22222222-2222-2222-2222-222222222222");

        TransactionRequest request = new TransactionRequest(
                transactionId,
                testUserId,
                new BigDecimal("250.00"),
                TransactionType.DEBIT
        );

        Transaction result =
                transactionService.processTransaction(request);

        Wallet updatedWallet =
                walletRepository.findWalletForRead(testUserId).orElseThrow();

        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        assertEquals(TransactionType.DEBIT, result.getType());

        assertEquals(
                new BigDecimal("750.00"),
                updatedWallet.getBalance()
        );

        assertEquals(1, transactionRepository.count());

        System.out.println("RESULT: Transaction processed successfully.");
        System.out.println("RESULT: Amount deducted = ₹250.00");
        System.out.println("RESULT: Final wallet balance = ₹"
                + updatedWallet.getBalance());
        System.out.println("RESULT: Transaction count = "
                + transactionRepository.count());
    }
}