import java.time.LocalDate;

public class Expense {
    private LocalDate date;
    private double amount;
    private String category;

    public Expense(LocalDate date, double amount, String category) {
        this.date = date;
        this.amount = amount;
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("Date: %s | Amount: $%.2f | Category: %s", 
            date, amount, category);
    }
}
