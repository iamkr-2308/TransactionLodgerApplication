package com.example.transactionlodger.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity                     //This tells JPA: "This Java class represents something that should be stored in the database."
@Table(name = "wallets")    //We're telling JPA that the table should be called: wallets. So conceptually H2 will have:
//wallets
//--------------------------------
//id | user_id | balance

public class Wallet {

    @Id                     //    @Id means: This is the primary key.
    @GeneratedValue         //    @GeneratedValue means: Let JPA/database generate the value.
    private UUID id;        //    Every wallet needs its own unique identifier.

    @Column(nullable = false, unique = true)
    private UUID userId;    //    This connects the wallet to the user.

    @Column(nullable = false, precision = 19, scale = 2)     // precision = 19 -- means the total number of digits allowed.
                                                             // scale = 2 -- means two digits after the decimal point.
                                                             // So things like: 500.00,250.00,100.50 are supported.
    private BigDecimal balance;   //    We should not use double for money. Example, floating-point calculations can produce values such as: 249.999999999.
                                  //    That's not something we want in a financial ledger. Instead: BigDecimal is appropriate for monetary values.
                                  //    So: ₹500.00, ₹250.00, ₹100.50 can be represented accurately.

    public Wallet() { }                                       // This is the no-argument constructor. JPA requires a no-argument constructor so it can create entity objects.

    public Wallet(UUID userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}