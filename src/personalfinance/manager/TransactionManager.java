package personalfinance.manager;

import personalfinance.model.Transaction;
import personalfinance.storage.FileHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransactionManager {

    private final List<Transaction> transactions;
    private int nextId;

    public TransactionManager() {
        this.transactions = new ArrayList<>(FileHandler.loadTransactions());
        this.nextId = transactions.stream()
                .map(Transaction::getId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    public void addTransaction(String description, double amount, String category, String type, String date) {
        Transaction transaction = new Transaction(nextId++, description, category, type.toLowerCase(), date, amount);
        transactions.add(transaction);
        FileHandler.saveTransactions(transactions);
    }

    public boolean deleteTransaction(int id) {
        boolean removed = transactions.removeIf(transaction -> transaction.getId() == id);
        if (removed) {
            FileHandler.saveTransactions(transactions);
        }
        return removed;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getExpenses() {
        return filterByType("expense");
    }

    public List<Transaction> getIncome() {
        return filterByType("income");
    }

    public List<Transaction> getByCategory(String category) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                filtered.add(transaction);
            }
        }
        return filtered;
    }

    public double getTotalIncome() {
        return sumAmounts(getIncome());
    }

    public double getTotalExpenses() {
        return sumAmounts(getExpenses());
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpenses();
    }

    public Transaction findById(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) {
                return transaction;
            }
        }
        return null;
    }

    private List<Transaction> filterByType(String type) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equalsIgnoreCase(type)) {
                filtered.add(transaction);
            }
        }
        return filtered;
    }

    private double sumAmounts(List<Transaction> items) {
        double total = 0.0;
        for (Transaction transaction : items) {
            total += transaction.getAmount();
        }
        return total;
    }
}
