package com.example.transactionlodger.repository;

import com.example.transactionlodger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // public interface TransactionRepository extends JpaRepository<Transaction, UUID>
    //Means: "This repository manages Transaction entities, and the primary key of a Transaction is a UUID."
    //So Spring gives us methods such as: save(), findById(), findAll(), delete(), deleteById(), existsById() automatically.


    Optional<Transaction> findByTransactionId(UUID transactionId);
// Spring understands: findByTransactionId and creates the database query for us.
//
//Conceptually: SELECT *
                //FROM transactions
                //WHERE transaction_id = ?
//
//So later, when request ABC arrives: 
//Request
//transactionId = ABC
//       ↓
//TransactionRepository
//       ↓
//"Does ABC already exist?"
//       ↓
//Yes / No

}