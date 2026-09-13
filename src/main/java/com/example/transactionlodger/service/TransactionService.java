//package com.example.transactionlodger.service;
//
//import com.example.transactionlodger.dto.TransactionRequest;
//import com.example.transactionlodger.entity.Transaction;
//import com.example.transactionlodger.repository.TransactionRepository;
//import com.example.transactionlodger.repository.WalletRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.example.transactionlodger.dto.TransactionRequest;
//import com.example.transactionlodger.entity.Transaction;
//
//@Service
//public class TransactionService {
//
//    private final WalletRepository walletRepository;
//    private final TransactionRepository transactionRepository;
//
//    public TransactionService(WalletRepository walletRepository,
//                              TransactionRepository transactionRepository) {
//        this.walletRepository = walletRepository;
//        this.transactionRepository = transactionRepository;
//    }
//
//    @Transactional
//    public Transaction processTransaction(TransactionRequest request) {
//
//        return null;
//    }
//}

package com.example.transactionlodger.service;

import com.example.transactionlodger.dto.TransactionRequest;
import com.example.transactionlodger.entity.Transaction;
import com.example.transactionlodger.entity.TransactionType;
import com.example.transactionlodger.exception.InsufficientFundsException;
import com.example.transactionlodger.exception.WalletNotFoundException;
import com.example.transactionlodger.repository.TransactionRepository;
import com.example.transactionlodger.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.transactionlodger.entity.Wallet;


@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(WalletRepository walletRepository,
                              TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction processTransaction(TransactionRequest request) {

        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (request.getType() == TransactionType.DEBIT) {

            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }

            wallet.setBalance(
                    wallet.getBalance().subtract(request.getAmount())
            );

        } else if (request.getType() == TransactionType.CREDIT) {

            wallet.setBalance(
                    wallet.getBalance().add(request.getAmount())
            );
        }

        Transaction transaction = new Transaction(
                request.getTransactionId(),
                request.getUserId(),
                request.getAmount(),
                request.getType()
        );

        return transactionRepository.save(transaction);
    }
}

