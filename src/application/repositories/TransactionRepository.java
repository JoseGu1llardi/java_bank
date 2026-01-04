package application.repositories;

import domain.entity.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class TransactionRepository {
    private Map<String, Transaction> transactions;
    private Map<String, List<String>> transactionsByAccountCode;

    public  TransactionRepository() {
        this.transactions = new HashMap<>();
        this.transactionsByAccountCode = new HashMap<>();
    }

    /**
     * Persists transaction and indexes by origin account
     */
    public void save(Transaction transaction) {
        String transactionId = transaction.getId();

        transactions.put(transactionId, transaction);

        // Update account index
        transactionsByAccountCode.computeIfAbsent(transaction.getOriginAccountCode(),
                k -> new ArrayList<>()).add(transactionId);
    }

    // Search transaction by ID
    public Optional<Transaction> getById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    public List<Transaction> getByAccountCode(String accountCode) {
        List<String> transactionsId = transactionsByAccountCode.getOrDefault(accountCode, Collections.emptyList());

        // Returns account transactions sorted by date descending
        return transactionsId.stream()
                .map(transactions::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Transaction::getDateTime).reversed())
                .collect(Collectors.toList());
    }

}
