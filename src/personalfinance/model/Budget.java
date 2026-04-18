package personalfinance.model;

public class Budget {

    private String category;
    private double limit;
    private double spent;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
        this.spent = 0.0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public double getRemaining() {
        return limit - spent;
    }

    public boolean isExceeded() {
        return spent > limit;
    }

    public void addSpent(double amount) {
        spent += amount;
    }

    @Override
    public String toString() {
        return "Budget{category='" + category + '\''
                + ", limit=" + limit
                + ", spent=" + spent
                + ", remaining=" + getRemaining()
                + '}';
    }
}
