package personalfinance.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import personalfinance.manager.BudgetManager;
import personalfinance.manager.ReportEngine;
import personalfinance.manager.TransactionManager;
import personalfinance.model.Budget;

import java.util.Map;

public class DashboardPanel extends ScrollPane {

    private final TransactionManager transactionManager;
    private final BudgetManager budgetManager;
    private final ReportEngine reportEngine;

    private final Label incomeValue = new Label();
    private final Label expenseValue = new Label();
    private final Label balanceValue = new Label();
    private final Label topCategoryValue = new Label();
    private final Label summaryLabel = new Label();
    private final Label expenseByCategoryLabel = new Label();
    private final Label incomeByCategoryLabel = new Label();
    private final Label monthlyTotalsLabel = new Label();
    private final VBox budgetAlertBox = new VBox(10);
    private final Label heroSummaryLabel = new Label();

    public DashboardPanel(TransactionManager transactionManager, BudgetManager budgetManager, ReportEngine reportEngine) {
        this.transactionManager = transactionManager;
        this.budgetManager = budgetManager;
        this.reportEngine = reportEngine;

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("page-pane");

        VBox heroCard = new VBox(12);
        heroCard.getStyleClass().addAll("hero-summary-card", "content-card");

        Label heroTitle = new Label("Financial Snapshot");
        heroTitle.getStyleClass().add("section-title-light");
        heroSummaryLabel.getStyleClass().add("hero-summary-text");
        heroSummaryLabel.setWrapText(true);
        heroCard.getChildren().addAll(heroTitle, heroSummaryLabel);

        HBox metrics = new HBox(16,
                createMetricCard("Total Income", incomeValue, "success-accent"),
                createMetricCard("Total Expenses", expenseValue, "danger-accent"),
                createMetricCard("Balance", balanceValue, "primary-accent"),
                createMetricCard("Top Expense", topCategoryValue, "warning-accent"));

        GridPane insightGrid = new GridPane();
        insightGrid.setHgap(16);
        insightGrid.setVgap(16);
        insightGrid.add(createInfoCard("Summary", summaryLabel), 0, 0);
        insightGrid.add(createInfoCard("Expenses By Category", expenseByCategoryLabel), 1, 0);
        insightGrid.add(createInfoCard("Income By Category", incomeByCategoryLabel), 0, 1);
        insightGrid.add(createInfoCard("Monthly Expense Totals", monthlyTotalsLabel), 1, 1);

        VBox budgetCard = new VBox(12);
        budgetCard.getStyleClass().addAll("content-card", "elevated-card");
        Label title = new Label("Budget Alerts");
        title.getStyleClass().add("section-title");
        budgetCard.getChildren().addAll(title, budgetAlertBox);

        content.getChildren().addAll(heroCard, metrics, insightGrid, budgetCard);

        setFitToWidth(true);
        setContent(content);
        getStyleClass().add("page-scroll");
    }

    public void refresh() {
        incomeValue.setText(currency(transactionManager.getTotalIncome()));
        expenseValue.setText(currency(transactionManager.getTotalExpenses()));
        balanceValue.setText(currency(transactionManager.getBalance()));
        topCategoryValue.setText(reportEngine.getTopSpendingCategory());
        heroSummaryLabel.setText("Current balance stands at " + currency(transactionManager.getBalance())
                + ". Top spending category: " + reportEngine.getTopSpendingCategory()
                + ". Review the cards below for category-wise trends and budget health.");

        summaryLabel.setText(reportEngine.getSummary());
        expenseByCategoryLabel.setText(formatMap(reportEngine.getExpensesByCategory()));
        incomeByCategoryLabel.setText(formatMap(reportEngine.getIncomeByCategory()));
        monthlyTotalsLabel.setText(formatMap(reportEngine.getMonthlyExpenseTotals()));
        refreshBudgetAlerts();
    }

    private VBox createMetricCard(String title, Label valueLabel, String accentStyle) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("metric-card", "elevated-card", accentStyle);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setFillWidth(true);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        valueLabel.getStyleClass().add("metric-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createInfoCard(String title, Label contentLabel) {
        VBox card = new VBox(12);
        card.getStyleClass().addAll("content-card", "glass-card");
        card.setMinHeight(200);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        contentLabel.getStyleClass().add("multi-line-value");
        contentLabel.setWrapText(true);
        VBox.setVgrow(contentLabel, Priority.ALWAYS);

        card.getChildren().addAll(titleLabel, contentLabel);
        return card;
    }

    private void refreshBudgetAlerts() {
        budgetAlertBox.getChildren().clear();

        if (budgetManager.getAllBudgets().isEmpty()) {
            Label empty = new Label("No budgets set yet.");
            empty.getStyleClass().add("muted-text");
            budgetAlertBox.getChildren().add(empty);
            return;
        }

        for (Budget budget : budgetManager.getAllBudgets()) {
            VBox item = new VBox(6);
            item.getStyleClass().add(budget.isExceeded() ? "budget-alert-danger" : "budget-alert");

            Label name = new Label(budget.getCategory());
            name.getStyleClass().add("budget-name");

            Label details = new Label(String.format(
                    "Limit: %s   Spent: %s   Remaining: %s",
                    currency(budget.getLimit()),
                    currency(budget.getSpent()),
                    currency(budget.getRemaining())));
            details.getStyleClass().add("budget-details");

            Label status = new Label(budget.isExceeded() ? "Exceeded" : "Within limit");
            status.getStyleClass().add(budget.isExceeded() ? "status-danger" : "status-ok");

            HBox row = new HBox(12, name, createSpacer(), status);
            item.getChildren().addAll(row, details);
            budgetAlertBox.getChildren().add(item);
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private String formatMap(Map<String, Double> data) {
        if (data.isEmpty()) {
            return "No data available.";
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            builder.append(entry.getKey())
                    .append("  -  ")
                    .append(currency(entry.getValue()))
                    .append('\n');
        }
        return builder.toString().trim();
    }

    private String currency(double amount) {
        return String.format("Rs. %.2f", amount);
    }
}
