package application.repositories;

import domain.entity.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
