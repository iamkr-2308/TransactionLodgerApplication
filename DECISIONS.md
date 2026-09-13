**# Design Decisions

## 1. Handling Concurrent Wallet Debits

### Problem

Multiple debit requests can arrive at the same time for the same wallet.

For example, if a wallet has ₹500 and multiple requests try to debit ₹100 simultaneously, two requests could potentially read the same balance before either request updates it.

Without proper concurrency control, this could result in an incorrect wallet balance or allow more money to be deducted than is available.

### Decision

The wallet is retrieved using a database-level pessimistic write lock:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Wallet> findByUserId(UUID userId);**