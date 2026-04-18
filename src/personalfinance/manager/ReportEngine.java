package personalfinance.manager;

import personalfinance.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportEngine {

    private final TransactionManager transactionManager;

    public ReportEngine(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Map<String, Double> getExpensesByCategory() {
        return collectByCategory(transactionManager.getExpenses());
    }

    public Map<String, Double> getIncomeByCategory() {
        return collectByCategory(transactionManager.getIncome());
    }

    public List<Transaction> getTransactionsByMonth(String yearMonth) {
        List<Transaction> monthlyTransactions = new ArrayList<>();
        for (Transaction transaction : transactionManager.getAllTransactions()) {
            if (transaction.getDate().startsWith(yearMonth)) {
                monthlyTransactions.add(transaction);
            }
        }
        return monthlyTransactions;
    }

    public Map<String, Double> getMonthlyExpenseTotals() {
        Map<String, Double> totals = new TreeMap<>();
        for (Transaction transaction : transactionManager.getExpenses()) {
            String yearMonth = transaction.getDate().substring(0, 7);
            totals.put(yearMonth, totals.getOrDefault(yearMonth, 0.0) + transaction.getAmount());
        }
        return totals;
    }

    public String getSummary() {
        return String.format(
                "Income: Rs. %.2f | Expenses: Rs. %.2f | Balance: Rs. %.2f",
                transactionManager.getTotalIncome(),
                transactionManager.getTotalExpenses(),
                transactionManager.getBalance());
    }

    public String getTopSpendingCategory() {
        Map<String, Double> totals = getExpensesByCategory();
        String topCategory = null;
        double highest = 0.0;

        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            if (entry.getValue() > highest) {
                topCategory = entry.getKey();
                highest = entry.getValue();
            }
        }

        if (topCategory == null) {
            return "No expenses yet";
        }
        return topCategory + " (Rs. " + String.format("%.2f", highest) + ")";
    }

    private Map<String, Double> collectByCategory(List<Transaction> transactions) {
        Map<String, Double> totals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            totals.put(category, totals.getOrDefault(category, 0.0) + transaction.getAmount());
        }
        return totals;
    }
}
