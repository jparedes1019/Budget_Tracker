import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BudgetTracker {
    private List<Expense> expenses;
    private int dailyTransactionGoal;
    private Scanner scanner;

    public BudgetTracker() {
        this.expenses = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        Welcome to Budget Tracker!      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        setDailyTransactionGoal();
        showMainMenu();
    }

    private void setDailyTransactionGoal() {
        while (true) {
            System.out.print("How many transactions do you want to make per day (goal)? ");
            try {
                dailyTransactionGoal = Integer.parseInt(scanner.nextLine().trim());
                if (dailyTransactionGoal > 0) {
                    System.out.println("Daily transaction goal set to: " + dailyTransactionGoal + " transactions/day");
                    System.out.println();
                    break;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("════════════════════════════════════════");
            System.out.println("           MAIN MENU");
            System.out.println("════════════════════════════════════════");
            System.out.println("1) Add an Expense");
            System.out.println("2) Weekly Summary");
            System.out.println("3) Monthly Summary");
            System.out.println("4) Exit");
            System.out.println("════════════════════════════════════════");
            System.out.print("Choose an option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addExpense();
                    break;
                case "2":
                    showWeeklySummary();
                    break;
                case "3":
                    showMonthlySummary();
                    break;
                case "4":
                    System.out.println("\nThank you for using Budget Tracker. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-4.\n");
            }
        }
    }

    private void addExpense() {
        System.out.println("\n── Add New Expense ──");

        LocalDate today = LocalDate.now();
        long todayTransactions = expenses.stream()
            .filter(e -> e.getDate().equals(today))
            .count();

        if (todayTransactions >= dailyTransactionGoal) {
            System.out.println("✗ Cannot add expense: You've already reached your daily transaction goal of " 
                + dailyTransactionGoal + " transactions today!");
            System.out.println("Expense not added.\n");
            return;
        }

        LocalDate date = getDateInput();
        double amount = getAmountInput();
        String category = getCategoryInput();

        Expense expense = new Expense(date, amount, category);
        expenses.add(expense);

        System.out.println("\n✓ Expense added successfully!");
        System.out.println(expense);
        System.out.println("Today's transactions: " + (todayTransactions + 1) + "/" + dailyTransactionGoal + "\n");
    }

    private LocalDate getDateInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        while (true) {
            System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return LocalDate.now();
            }

            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD (e.g., 2025-11-06)");
            }
        }
    }

    private double getAmountInput() {
        while (true) {
            System.out.print("Enter amount ($): ");
            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount > 0) {
                    return amount;
                } else {
                    System.out.println("Amount must be positive.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number (e.g., 25.50)");
            }
        }
    }

    private String getCategoryInput() {
        System.out.print("Enter category (e.g., Food, Transport, Entertainment): ");
        String category = scanner.nextLine().trim();
        return category.isEmpty() ? "Other" : category;
    }

    private void showWeeklySummary() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         WEEKLY SUMMARY (7 Days)        ║");
        System.out.println("╚════════════════════════════════════════╝");

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<Expense> weekExpenses = expenses.stream()
            .filter(e -> !e.getDate().isBefore(weekAgo))
            .toList();

        if (weekExpenses.isEmpty()) {
            System.out.println("No expenses recorded in the past 7 days.\n");
            return;
        }

        double totalAmount = weekExpenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum();

        int transactionCount = weekExpenses.size();
        double averageDaily = totalAmount / 7;

        System.out.println("Period: " + weekAgo + " to " + today);
        System.out.println("────────────────────────────────────────");
        System.out.printf("Total Expenses:        $%.2f%n", totalAmount);
        System.out.printf("Total Transactions:    %d%n", transactionCount);
        System.out.printf("Average Daily Spend:   $%.2f%n", averageDaily);
        System.out.printf("Avg Transactions/Day:  %.1f / %d (goal)%n", 
            transactionCount / 7.0, dailyTransactionGoal);
        System.out.println("────────────────────────────────────────");
        System.out.println("\nRecent Expenses:");
        weekExpenses.forEach(e -> System.out.println("  • " + e));
        System.out.println();
    }

    private void showMonthlySummary() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        MONTHLY SUMMARY (30 Days)       ║");
        System.out.println("╚════════════════════════════════════════╝");

        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(29);

        List<Expense> monthExpenses = expenses.stream()
            .filter(e -> !e.getDate().isBefore(monthAgo))
            .toList();

        if (monthExpenses.isEmpty()) {
            System.out.println("No expenses recorded in the past 30 days.\n");
            return;
        }

        double totalAmount = monthExpenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum();

        int transactionCount = monthExpenses.size();
        double averageDaily = totalAmount / 30;

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : monthExpenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        System.out.println("Period: " + monthAgo + " to " + today);
        System.out.println("────────────────────────────────────────");
        System.out.printf("Total Expenses:        $%.2f%n", totalAmount);
        System.out.printf("Total Transactions:    %d%n", transactionCount);
        System.out.printf("Average Daily Spend:   $%.2f%n", averageDaily);
        System.out.printf("Avg Transactions/Day:  %.1f / %d (goal)%n", 
            transactionCount / 30.0, dailyTransactionGoal);
        System.out.println("────────────────────────────────────────");
        System.out.println("\nSpending by Category:");
        categoryTotals.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> 
                System.out.printf("  %-20s $%.2f (%.1f%%)%n", 
                    entry.getKey() + ":", 
                    entry.getValue(),
                    (entry.getValue() / totalAmount) * 100)
            );
        System.out.println();
    }

    public static void main(String[] args) {
        BudgetTracker tracker = new BudgetTracker();
        tracker.start();
    }
}
