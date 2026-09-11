package com.example.transactionlodger.repository;

import com.example.transactionlodger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
// interface public interface WalletRepository --  We aren't creating a normal class. Spring Data JPA will create the implementation for us.

// extends JpaRepository extends JpaRepository<Wallet, UUID> -- This is powerful. We're telling Spring: "This repository works with the Wallet entity, and Wallet's primary key is a UUID."
//Because of this, Spring automatically gives us methods such as: save(), findById(), findAll(), delete(), deleteById(), count(), existsById()
//We don't have to write SQL for these basic operations.

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByUserId(UUID userId);
    // This method looks almost like English: Find Wallet by User ID.
    // Spring Data JPA understands the method name: findBy + UserId and generates the appropriate query.

    // Optional<Wallet>? Suppose we search for: userId = ABC
    // There might be a wallet: ABC → ₹500 or there might not be one.
    // So instead of returning null, we use: Optional<Wallet>. It represents: Wallet found OR Wallet not found

    // @Lock(LockModeType.PESSIMISTIC_WRITE)        //The first line tells JPA: Lock the row.
    // Optional<Wallet> findByUserId(UUID userId)   //The second tells JPA: Find the wallet belonging to this user.
    // Together: find wallet + lock wallet -- That's the foundation of our concurrency solution.


    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    Optional<Wallet> findWalletForRead(@Param("userId") UUID userId);

}