package com.example.transactionlodger.config;

import com.example.transactionlodger.entity.Wallet;
import com.example.transactionlodger.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(WalletRepository walletRepository) {

        return args -> {

            UUID userId = UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

            Wallet wallet = new Wallet(
                    userId,
                    new BigDecimal("1000.00")
            );

            walletRepository.save(wallet);

            System.out.println(
                    "TEST WALLET CREATED -> User ID: "
                            + userId
                            + " | Balance: ₹1000.00"
            );
        };
    }
}