package personalfinance.storage;

import personalfinance.model.Budget;
import personalfinance.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileHandler {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path TRANSACTIONS_FILE = DATA_DIR.resolve("transactions.csv");
    private static final Path BUDGETS_FILE = DATA_DIR.resolve("budgets.csv");

    private FileHandler() {
    }

    public static void init() {
        try {
            Files.createDirectories(DATA_DIR);
            if (Files.notExists(TRANSACTIONS_FILE)) {
                Files.createFile(TRANSACTIONS_FILE);
            }
            if (Files.notExists(BUDGETS_FILE)) {
                Files.createFile(BUDGETS_FILE);
            }
            seedDemoDataIfNeeded();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize data storage.", exception);
        }
    }

    public static void saveTransactions(List<Transaction> transactions) {
        writeLines(TRANSACTIONS_FILE, transactions.stream()
                .map(transaction -> String.join(",",
                        String.valueOf(transaction.getId()),
                        escape(transaction.getDescription()),
                        escape(transaction.getCategory()),
                        escape(transaction.getType()),
                        escape(transaction.getDate()),
                        String.valueOf(transaction.getAmount())))
                .toList());
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        if (Files.notExists(TRANSACTIONS_FILE)) {
            return transactions;
        }

        try (BufferedReader reader = Files.newBufferedReader(TRANSACTIONS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length != 6) {
                    continue;
                }

                transactions.add(new Transaction(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        Double.parseDouble(parts[5])));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load transactions.", exception);
        }

        return transactions;
    }

    public static void saveBudgets(List<Budget> budgets) {
        writeLines(BUDGETS_FILE, budgets.stream()
                .map(budget -> String.join(",",
                        escape(budget.getCategory()),
                        String.valueOf(budget.getLimit()),
                        String.valueOf(budget.getSpent())))
                .toList());
    }

    public static List<Budget> loadBudgets() {
        List<Budget> budgets = new ArrayList<>();
        if (Files.notExists(BUDGETS_FILE)) {
            return budgets;
        }

        try (BufferedReader reader = Files.newBufferedReader(BUDGETS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                if (parts.length != 3) {
                    continue;
                }

                Budget budget = new Budget(parts[0], Double.parseDouble(parts[1]));
                budget.setSpent(Double.parseDouble(parts[2]));
                budgets.add(budget);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load budgets.", exception);
        }

        return budgets;
    }

    private static void writeLines(Path path, List<String> lines) {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize data storage.", exception);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save data to " + path + '.', exception);
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.replace("\"", "\"\"");
        if (normalized.contains(",") || normalized.contains("\"")) {
            return '"' + normalized + '"';
        }
        return normalized;
    }

    private static String[] parseCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char ch = line.charAt(index);
            if (ch == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        parts.add(current.toString());
        return parts.toArray(String[]::new);
    }

    private static void seedDemoDataIfNeeded() throws IOException {
        if (Files.size(TRANSACTIONS_FILE) == 0) {
            Files.write(TRANSACTIONS_FILE, List.of(
                    "1,Monthly allowance,Pocket Money,income,2026-04-01,12000.0",
                    "2,Freelance poster design,Freelance,income,2026-04-05,4500.0",
                    "3,Hostel mess payment,Food,expense,2026-04-02,2200.0",
                    "4,Metro recharge,Transport,expense,2026-04-04,650.0",
                    "5,Movie night,Entertainment,expense,2026-04-07,480.0",
                    "6,Semester books,Education,expense,2026-04-10,1600.0",
                    "7,Part-time tutoring,Scholarship,income,2026-04-12,3000.0",
                    "8,Online shopping,Shopping,expense,2026-04-14,1350.0"));
        }

        if (Files.size(BUDGETS_FILE) == 0) {
            Files.write(BUDGETS_FILE, List.of(
                    "Food,5000.0,2200.0",
                    "Transport,1500.0,650.0",
                    "Entertainment,1200.0,480.0",
                    "Education,3000.0,1600.0",
                    "Shopping,2500.0,1350.0"));
        }
    }
}
