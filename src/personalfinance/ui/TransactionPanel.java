package personalfinance.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import personalfinance.manager.BudgetManager;
import personalfinance.manager.TransactionManager;
import personalfinance.model.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TransactionPanel extends BorderPane {

    private final TransactionManager transactionManager;
    private final BudgetManager budgetManager;
    private final Runnable refreshCallback;

    private final TextField descriptionField = new TextField();
    private final TextField amountField = new TextField();
    private final ComboBox<String> typeBox = new ComboBox<>();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField dateField = new TextField(LocalDate.now().toString());
    private final Label feedbackLabel = new Label();
    private final Label helperLabel = new Label();
    private final TableView<Transaction> transactionTable = new TableView<>();

    public TransactionPanel(TransactionManager transactionManager, BudgetManager budgetManager, Runnable refreshCallback) {
        this.transactionManager = transactionManager;
        this.budgetManager = budgetManager;
        this.refreshCallback = refreshCallback;

        setPadding(new Insets(20));
        getStyleClass().add("page-pane");

        VBox formCard = buildFormCard();
        VBox tableCard = buildTableCard();

        setTop(formCard);
        setCenter(tableCard);
    }

    public void refreshTable() {
        transactionTable.setItems(FXCollections.observableArrayList(transactionManager.getAllTransactions()));
    }

    private VBox buildFormCard() {
        VBox card = new VBox(16);
        card.getStyleClass().addAll("content-card", "glass-card");

        Label title = new Label("Add Transaction");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Use quick presets for college-life income and expense entries.");
        subtitle.getStyleClass().add("panel-subtitle");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(14);
        formGrid.setVgap(14);

        typeBox.setItems(FXCollections.observableArrayList("expense", "income"));
        typeBox.getSelectionModel().selectFirst();
        typeBox.setOnAction(event -> syncCategories());
        categoryBox.setVisibleRowCount(8);
        syncCategories();

        addField(formGrid, 0, 0, "Description", descriptionField);
        addField(formGrid, 1, 0, "Amount", amountField);
        addField(formGrid, 2, 0, "Category", categoryBox);
        addField(formGrid, 0, 1, "Type", typeBox);
        addField(formGrid, 1, 1, "Date (yyyy-MM-dd)", dateField);

        Button addButton = new Button("Add Transaction");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> addTransaction());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> clearForm());

        feedbackLabel.getStyleClass().add("muted-text");
        helperLabel.getStyleClass().add("helper-chip");
        updateHelperText();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12, addButton, clearButton, helperLabel, spacer, feedbackLabel);
        actions.setPadding(new Insets(4, 0, 0, 0));

        card.getChildren().addAll(title, subtitle, formGrid, actions);
        return card;
    }

    private VBox buildTableCard() {
        VBox card = new VBox(14);
        card.getStyleClass().addAll("content-card", "elevated-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label title = new Label("Transactions");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Recent financial activity appears here with formatted amounts.");
        subtitle.getStyleClass().add("panel-subtitle");

        configureTable();
        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteSelectedTransaction());

        card.getChildren().addAll(title, subtitle, transactionTable, deleteButton);
        return card;
    }

    private void configureTable() {
        transactionTable.getStyleClass().add("finance-table");
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        transactionTable.setPlaceholder(new Label("No transactions added yet."));

        TableColumn<Transaction, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));

        TableColumn<Transaction, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.format("Rs. %.2f", cell.getValue().getAmount())));

        transactionTable.getColumns().setAll(idColumn, descriptionColumn, categoryColumn, typeColumn, dateColumn, amountColumn);
    }

    private void addTransaction() {
        String description = descriptionField.getText().trim();
        String category = categoryBox.getValue();
        String type = typeBox.getValue();
        String date = dateField.getText().trim();

        if (description.isEmpty() || category.isEmpty() || date.isEmpty()) {
            showFeedback("Please fill all fields.", true);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showFeedback("Amount must be greater than 0.", true);
                return;
            }
        } catch (NumberFormatException exception) {
            showFeedback("Enter a valid numeric amount.", true);
            return;
        }

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            showFeedback("Date must be in yyyy-MM-dd format.", true);
            return;
        }

        transactionManager.addTransaction(description, amount, category, type, date);
        if ("expense".equalsIgnoreCase(type)) {
            budgetManager.recordExpense(category, amount);
        }

        clearForm();
        showFeedback("Transaction added successfully.", false);
        refreshCallback.run();
    }

    private void deleteSelectedTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showFeedback("Select a transaction to delete.", true);
            return;
        }

        boolean deleted = transactionManager.deleteTransaction(selected.getId());
        if (deleted && "expense".equalsIgnoreCase(selected.getType())) {
            budgetManager.removeExpense(selected.getCategory(), selected.getAmount());
        }

        showFeedback("Transaction deleted.", false);
        refreshCallback.run();
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        typeBox.getSelectionModel().selectFirst();
        syncCategories();
        dateField.setText(LocalDate.now().toString());
    }

    private void addField(GridPane grid, int col, int row, String labelText, javafx.scene.Node input) {
        VBox wrapper = new VBox(8);
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        input.getStyleClass().add("field-input");
        wrapper.getChildren().addAll(label, input);
        grid.add(wrapper, col, row);
        GridPane.setHgrow(wrapper, Priority.ALWAYS);
    }

    private void showFeedback(String message, boolean error) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("error-text", "success-text");
        feedbackLabel.getStyleClass().add(error ? "error-text" : "success-text");
    }

    private void syncCategories() {
        if ("income".equalsIgnoreCase(typeBox.getValue())) {
            categoryBox.setItems(CategoryCatalog.getIncomeCategories());
        } else {
            categoryBox.setItems(CategoryCatalog.getExpenseCategories());
        }
        categoryBox.getSelectionModel().selectFirst();
        updateHelperText();
    }

    private void updateHelperText() {
        String mode = typeBox.getValue() == null ? "expense" : typeBox.getValue();
        helperLabel.setText("Preset " + mode + " categories");
    }
}
