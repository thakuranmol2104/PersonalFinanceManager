package personalfinance.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class CategoryCatalog {

    private static final ObservableList<String> EXPENSE_CATEGORIES = FXCollections.observableArrayList(
            "Food",
            "Transport",
            "Rent",
            "Shopping",
            "Entertainment",
            "Health",
            "Education",
            "Utilities");

    private static final ObservableList<String> INCOME_CATEGORIES = FXCollections.observableArrayList(
            "Salary",
            "Freelance",
            "Scholarship",
            "Pocket Money",
            "Investment",
            "Gift");

    private CategoryCatalog() {
    }

    public static ObservableList<String> getExpenseCategories() {
        return FXCollections.observableArrayList(EXPENSE_CATEGORIES);
    }

    public static ObservableList<String> getIncomeCategories() {
        return FXCollections.observableArrayList(INCOME_CATEGORIES);
    }
}
