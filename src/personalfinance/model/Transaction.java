package personalfinance.model;

public class Transaction {

    private int id;
    private String description;
    private String category;
    private String type;
    private String date;
    private double amount;

    public Transaction(int id, String description, String category, String type, String date, double amount) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.type = type;
        this.date = date;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id
                + ", description='" + description + '\''
                + ", category='" + category + '\''
                + ", type='" + type + '\''
                + ", date='" + date + '\''
                + ", amount=" + amount
                + '}';
    }
}
