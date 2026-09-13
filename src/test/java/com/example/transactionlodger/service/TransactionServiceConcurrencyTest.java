package com.example.transactionlodger.service;

import com.example.transactionlodger.entity.Transaction;
import com.example.transactionlodger.entity.TransactionType;
import com.example.transactionlodger.entity.Wallet;
import com.example.transactionlodger.repository.TransactionRepository;
import com.example.transactionlodger.repository.WalletRepository;
import com.example.transactionlodger.dto.TransactionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {

        transactionRepository.deleteAll();
        walletRepository.deleteAll();

        walletRepository.save(
                new Wallet(userId, new BigDecimal("1000.00"))
        );
    }

    @AfterEach
    void tearDown() {

        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    @DisplayName("Sends 10 concurrent debit requests of ₹100 for a wallet with a ₹500 balance. Ensures the final balance is exactly ₹0 and 5 requests fail with insufficient funds.")
    void sendsTenConcurrentDebitsWithLimitedBalance() throws Exception {

        System.out.println();
        System.out.println("======================================================");
        System.out.println("TEST: 10 CONCURRENT DEBITS WITH ₹500 WALLET BALANCE");
        System.out.println("======================================================");
        System.out.println("Initial balance: ₹500.00");
        System.out.println("Transaction amount: ₹100.00");
        System.out.println("Concurrent requests: 10");

        walletRepository.deleteAll();

        walletRepository.save(
                new Wallet(userId, new BigDecimal("500.00"))
        );

        ExecutorService executor =
                Executors.newFixedThreadPool(10);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        List<Future<Transaction>> futures =
                new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            UUID transactionId = UUID.randomUUID();

            TransactionRequest request =
                    new TransactionRequest(
                            transactionId,
                            userId,
                            new BigDecimal("100.00"),
                            TransactionType.DEBIT
                    );

            futures.add(
                    executor.submit(() -> {

                        startSignal.await();

                        return transactionService
                                .processTransaction(request);
                    })
            );
        }

        startSignal.countDown();

        int successfulRequests = 0;
        int insufficientFundsRequests = 0;

        for (Future<Transaction> future : futures) {

            try {

                future.get();
                successfulRequests++;

            } catch (ExecutionException e) {

                if (e.getCause() instanceof RuntimeException
                        && "Insufficient funds"
                        .equals(e.getCause().getMessage())) {

                    insufficientFundsRequests++;

                } else {

                    throw e;
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Wallet wallet = walletRepository
                .findWalletForRead(userId)
                .orElseThrow();

        long transactionCount =
                transactionRepository.count();

        System.out.println(
                "Successful requests: "
                        + successfulRequests
        );

        System.out.println(
                "Insufficient funds requests: "
                        + insufficientFundsRequests
        );

        System.out.println(
                "Transactions stored in DB: "
                        + transactionCount
        );

        System.out.println(
                "Final balance: ₹"
                        + wallet.getBalance()
        );

        System.out.println(
                "======================================================"
        );

        assertEquals(
                5,
                successfulRequests,
                "Exactly 5 debit requests should succeed"
        );

        assertEquals(
                5,
                insufficientFundsRequests,
                "Exactly 5 requests should fail due to insufficient funds"
        );

        assertEquals(
                new BigDecimal("0.00"),
                wallet.getBalance(),
                "Final balance should be exactly ₹0"
        );

        assertEquals(
                5,
                transactionCount,
                "Exactly 5 successful transactions should be stored"
        );
    }

    @Test
    @DisplayName("Sends 3 identical transactionIDs simultaneously. Ensures the balance is only deducted once.")
    void sendsThreeIdenticalTransactionsSimultaneously() throws Exception {

        UUID transactionId =
                UUID.fromString("33333333-3333-3333-3333-333333333333");

        TransactionRequest request =
                new TransactionRequest(
                        transactionId,
                        userId,
                        new BigDecimal("250.00"),
                        TransactionType.DEBIT
                );

        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        List<Future<Transaction>> futures =
                new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            futures.add(
                    executor.submit(() -> {

                        startSignal.await();

                        return transactionService
                                .processTransaction(request);
                    })
            );
        }

        startSignal.countDown();

        int successfulRequests = 0;
        int failedRequests = 0;

        for (Future<Transaction> future : futures) {

            try {

                future.get();
                successfulRequests++;

            } catch (ExecutionException e) {

                failedRequests++;
            }
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Wallet wallet = walletRepository
                .findWalletForRead(userId)
                .orElseThrow();

        long transactionCount =
                transactionRepository.count();

        System.out.println();
        System.out.println("======================================================");
        System.out.println("TEST RESULT: DUPLICATE TRANSACTION");
        System.out.println("======================================================");

        System.out.println(
                "Successful requests: "
                        + successfulRequests
        );

        System.out.println(
                "Failed duplicate requests: "
                        + failedRequests
        );

        System.out.println(
                "Transactions stored in DB: "
                        + transactionCount
        );

        System.out.println(
                "Final balance: ₹"
                        + wallet.getBalance()
        );

        System.out.println("======================================================");

        assertEquals(
                1,
                successfulRequests,
                "Exactly 1 duplicate request should succeed"
        );

        assertEquals(
                2,
                failedRequests,
                "Exactly 2 duplicate requests should fail"
        );

        assertEquals(
                new BigDecimal("750.00"),
                wallet.getBalance(),
                "Balance should be deducted only once"
        );

        assertEquals(
                1,
                transactionCount,
                "Only 1 transaction should be stored"
        );

    }

    @Test
    @DisplayName("Processes a single valid debit transaction successfully.")
    void processesSingleValidDebitTransactionSuccessfully() {

        System.out.println();
        System.out.println("======================================================");
        System.out.println("TEST: SINGLE VALID DEBIT TRANSACTION");
        System.out.println("======================================================");
        System.out.println("Initial balance: ₹1000.00");
        System.out.println("Transaction amount: ₹250.00");

        UUID transactionId = UUID.randomUUID();

        TransactionRequest request = new TransactionRequest(
                transactionId,
                userId,
                new BigDecimal("250.00"),
                TransactionType.DEBIT
        );

        Transaction transaction =
                transactionService.processTransaction(request);

        Wallet wallet = walletRepository
                .findWalletForRead(userId)
                .orElseThrow();

        long transactionCount =
                transactionRepository.count();

        System.out.println(
                "Transaction processed: "
                        + transaction.getTransactionId()
        );

        System.out.println(
                "Transactions stored in DB: "
                        + transactionCount
        );

        System.out.println(
                "Final balance: ₹"
                        + wallet.getBalance()
        );

        System.out.println("======================================================");

        assertNotNull(transaction);

        assertEquals(
                transactionId,
                transaction.getTransactionId()
        );

        assertEquals(
                new BigDecimal("750.00"),
                wallet.getBalance()
        );

        assertEquals(
                1,
                transactionCount
        );
    }
    }