package personalfinance.manager;

import personalfinance.model.Budget;
import personalfinance.storage.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class BudgetManager {

    private final List<Budget> budgets;

    public BudgetManager() {
        this.budgets = new ArrayList<>(FileHandler.loadBudgets());
    }

    public void setBudget(String category, double limit) {
        Budget existing = findByCategory(category);
        if (existing == null) {
            budgets.add(new Budget(category, limit));
        } else {
            existing.setLimit(limit);
        }
        FileHandler.saveBudgets(budgets);
    }

    public void recordExpense(String category, double amount) {
        Budget budget = findByCategory(category);
        if (budget != null) {
            budget.addSpent(amount);
            FileHandler.saveBudgets(budgets);
        }
    }

    public void removeExpense(String category, double amount) {
        Budget budget = findByCategory(category);
        if (budget != null) {
            budget.setSpent(Math.max(0.0, budget.getSpent() - amount));
            FileHandler.saveBudgets(budgets);
        }
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }

    public List<Budget> getExceededBudgets() {
        List<Budget> exceeded = new ArrayList<>();
        for (Budget budget : budgets) {
            if (budget.isExceeded()) {
                exceeded.add(budget);
            }
        }
        return exceeded;
    }

    public Budget findByCategory(String category) {
        for (Budget budget : budgets) {
            if (budget.getCategory().equalsIgnoreCase(category)) {
                return budget;
            }
        }
        return null;
    }

    public boolean deleteBudget(String category) {
        boolean removed = budgets.removeIf(budget -> budget.getCategory().equalsIgnoreCase(category));
        if (removed) {
            FileHandler.saveBudgets(budgets);
        }
        return removed;
    }

    public double getSpentPercentage(String category) {
        Budget budget = findByCategory(category);
        if (budget == null || budget.getLimit() <= 0) {
            return 0.0;
        }
        return (budget.getSpent() / budget.getLimit()) * 100.0;
    }
}
