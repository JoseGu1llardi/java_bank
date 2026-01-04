package application.repositories;

import domain.entity.Transaction;
import domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionRepository {
    private Map<String, Transaction> transactions;
    private Map<String, List<String>> transactionsByAccount;

    public  TransactionRepository() {
        this.transactions = new HashMap<>();
        this.transactionsByAccount = new HashMap<>();
    }

    /**
     * Persists transaction and indexes by origin account
     */
    public void save(Transaction transaction) {
        String transactionId = transaction.getId();

        transactions.put(transactionId, transaction);

        // Update account index
        transactionsByAccount.computeIfAbsent(transaction.getOriginAccountCode(),
                k -> new ArrayList<>()).add(transactionId);
    }

    // Search transaction by ID
    public Optional<Transaction> getById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    /**
     * Search all transaction of an account - O(n) account transactions
     */
    public List<Transaction> getByAccountCode(String accountCode) {
        List<String> transactionsId = transactionsByAccount.getOrDefault(accountCode, Collections.emptyList());

        // Returns account transactions sorted by date descending
        return transactionsId.stream()
                .map(transactions::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Transaction::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Search for transactions of an account in a specific period
     */
    public List<Transaction> searchForAccountAndDate(String accountCode,
                                                     LocalDateTime startDate, LocalDateTime endDate) {
        // Filters account transactions by date range
        return getByAccountCode(accountCode).stream()
                .filter( t -> !t.getDateTime().isBefore(startDate) &&
                        !t.getDateTime().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Get transaction by type
     */
    public List<Transaction> getByType(String accountCode, TransactionType  type) {
        return getByAccountCode(accountCode).stream()
                .filter( t -> t.getType().equals(type))
                .collect(Collectors.toList());
    }

    /**
     * The code takes all transaction from an account, separates them by type
     * and adds up the values of each type, returning the total amount of
     * money moved in each category
     */
    public Map<TransactionType, BigDecimal> calculateTotalByType(String accountCode, LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        return searchForAccountAndDate(accountCode, startDate, endDate).stream()
                .collect(Collectors.groupingBy(
                        Transaction::getType,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    /**
     * Fetch the latest N transactions
     */
    public List<Transaction> fetchLatest(String accountCode, long quantity) {
        return getByAccountCode(accountCode).stream()
                .limit(quantity)
                .collect(Collectors.toList());
    }

    /**
     * Count how many transactions an account has
     */
    public long countTransactions(String accountCode) {
        return transactionsByAccount.getOrDefault(accountCode, Collections.emptyList()).size();
    }

}
