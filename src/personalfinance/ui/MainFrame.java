package personalfinance.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import personalfinance.manager.BudgetManager;
import personalfinance.manager.ReportEngine;
import personalfinance.manager.TransactionManager;

import java.io.File;

public class MainFrame {

    private final TransactionManager transactionManager;
    private final BudgetManager budgetManager;
    private final ReportEngine reportEngine;

    private final DashboardPanel dashboardPanel;
    private final TransactionPanel transactionPanel;
    private final BudgetPanel budgetPanel;

    public MainFrame() {
        this.transactionManager = new TransactionManager();
        this.budgetManager = new BudgetManager();
        this.reportEngine = new ReportEngine(transactionManager);

        this.dashboardPanel = new DashboardPanel(transactionManager, budgetManager, reportEngine);
        this.transactionPanel = new TransactionPanel(transactionManager, budgetManager, this::refreshAll);
        this.budgetPanel = new BudgetPanel(budgetManager, this::refreshAll);
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setTop(buildHeader());
        root.setCenter(buildTabs());

        Scene scene = new Scene(root, 1240, 820);
        File stylesheet = new File("src/personalfinance/ui/style.css");
        scene.getStylesheets().add(stylesheet.toURI().toString());

        stage.setTitle("Personal Finance Manager");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(760);
        stage.centerOnScreen();

        refreshAll();
        stage.show();
    }

    public void refreshAll() {
        dashboardPanel.refresh();
        transactionPanel.refreshTable();
        budgetPanel.refreshTable();
    }

    private VBox buildHeader() {
        Label titleLabel = new Label("Personal Finance Manager");
        titleLabel.getStyleClass().add("app-title");

        Label subtitleLabel = new Label("Track income, monitor expenses, and stay ahead of every budget.");
        subtitleLabel.getStyleClass().add("app-subtitle");

        VBox textBox = new VBox(6, titleLabel, subtitleLabel);

        Label badge = new Label("JavaFX Desktop");
        badge.getStyleClass().add("hero-badge");

        Label summary = new Label("3 modules  •  CSV storage  •  MVC architecture");
        summary.getStyleClass().add("hero-meta");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(16, textBox, spacer, badge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(10, topRow, summary);
        header.setPadding(new Insets(24, 28, 20, 28));
        header.getStyleClass().add("hero-panel");
        return header;
    }

    private TabPane buildTabs() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("main-tabs");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab dashboardTab = new Tab("Dashboard", dashboardPanel);
        Tab transactionsTab = new Tab("Transactions", transactionPanel);
        Tab budgetsTab = new Tab("Budgets", budgetPanel);

        tabPane.getTabs().addAll(dashboardTab, transactionsTab, budgetsTab);
        BorderPane.setMargin(tabPane, new Insets(20, 24, 24, 24));
        return tabPane;
    }
}
