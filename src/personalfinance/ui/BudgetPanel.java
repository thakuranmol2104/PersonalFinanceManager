package personalfinance.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import personalfinance.manager.BudgetManager;
import personalfinance.model.Budget;

public class BudgetPanel extends BorderPane {

    private final BudgetManager budgetManager;
    private final Runnable refreshCallback;

    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField limitField = new TextField();
    private final Label feedbackLabel = new Label();
    private final Label helperLabel = new Label();
    private final TableView<Budget> budgetTable = new TableView<>();

    public BudgetPanel(BudgetManager budgetManager, Runnable refreshCallback) {
        this.budgetManager = budgetManager;
        this.refreshCallback = refreshCallback;

        setPadding(new Insets(20));
        getStyleClass().add("page-pane");

        setTop(buildFormCard());
        setCenter(buildTableCard());
    }

    public void refreshTable() {
        budgetTable.setItems(FXCollections.observableArrayList(budgetManager.getAllBudgets()));
    }

    private VBox buildFormCard() {
        VBox card = new VBox(16);
        card.getStyleClass().addAll("content-card", "glass-card");

        Label title = new Label("Set Budget");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Choose from predefined expense categories and assign a monthly limit.");
        subtitle.getStyleClass().add("panel-subtitle");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(14);
        formGrid.setVgap(14);

        categoryBox.setItems(CategoryCatalog.getExpenseCategories());
        categoryBox.getSelectionModel().selectFirst();

        addField(formGrid, 0, "Category", categoryBox);
        addField(formGrid, 1, "Limit", limitField);

        Button saveButton = new Button("Save Budget");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(event -> saveBudget());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> clearForm());

        feedbackLabel.getStyleClass().add("muted-text");
        helperLabel.getStyleClass().add("helper-chip");
        helperLabel.setText("Expense categories only");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(12, saveButton, clearButton, helperLabel, spacer, feedbackLabel);

        card.getChildren().addAll(title, subtitle, formGrid, actions);
        return card;
    }

    private VBox buildTableCard() {
        VBox card = new VBox(14);
        card.getStyleClass().addAll("content-card", "elevated-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label title = new Label("Budget Overview");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Monitor spent amount, remaining budget, and alert status.");
        subtitle.getStyleClass().add("panel-subtitle");

        configureTable();
        VBox.setVgrow(budgetTable, Priority.ALWAYS);

        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteBudget());

        card.getChildren().addAll(title, subtitle, budgetTable, deleteButton);
        return card;
    }

    private void configureTable() {
        budgetTable.getStyleClass().add("finance-table");
        budgetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        budgetTable.setPlaceholder(new Label("No budgets created yet."));

        TableColumn<Budget, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCategory()));

        TableColumn<Budget, String> limitColumn = new TableColumn<>("Limit");
        limitColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.format("Rs. %.2f", cell.getValue().getLimit())));

        TableColumn<Budget, String> spentColumn = new TableColumn<>("Spent");
        spentColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.format("Rs. %.2f", cell.getValue().getSpent())));

        TableColumn<Budget, String> remainingColumn = new TableColumn<>("Remaining");
        remainingColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.format("Rs. %.2f", cell.getValue().getRemaining())));

        TableColumn<Budget, String> percentColumn = new TableColumn<>("Used %");
        percentColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                String.format("%.1f%%", budgetManager.getSpentPercentage(cell.getValue().getCategory()))));

        TableColumn<Budget, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().isExceeded() ? "Exceeded" : "Within limit"));

        budgetTable.getColumns().setAll(categoryColumn, limitColumn, spentColumn, remainingColumn, percentColumn, statusColumn);
    }

    private void saveBudget() {
        String category = categoryBox.getValue();
        if (category.isEmpty()) {
            showFeedback("Please enter a category.", true);
            return;
        }

        double limit;
        try {
            limit = Double.parseDouble(limitField.getText().trim());
            if (limit <= 0) {
                showFeedback("Budget limit must be greater than 0.", true);
                return;
            }
        } catch (NumberFormatException exception) {
            showFeedback("Enter a valid numeric budget limit.", true);
            return;
        }

        budgetManager.setBudget(category, limit);
        clearForm();
        showFeedback("Budget saved successfully.", false);
        refreshCallback.run();
    }

    private void deleteBudget() {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Select a budget to delete.", true);
            return;
        }

        budgetManager.deleteBudget(selected.getCategory());
        showFeedback("Budget deleted.", false);
        refreshCallback.run();
    }

    private void addField(GridPane grid, int columnIndex, String labelText, javafx.scene.Node input) {
        VBox wrapper = new VBox(8);
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        input.getStyleClass().add("field-input");
        wrapper.getChildren().addAll(label, input);
        grid.add(wrapper, columnIndex, 0);
        GridPane.setHgrow(wrapper, Priority.ALWAYS);
    }

    private void clearForm() {
        categoryBox.getSelectionModel().selectFirst();
        limitField.clear();
    }

    private void showFeedback(String message, boolean error) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("error-text", "success-text");
        feedbackLabel.getStyleClass().add(error ? "error-text" : "success-text");
    }
}
