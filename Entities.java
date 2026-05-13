import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

class UserCredentials implements Serializable {
    private int pin;
    public UserCredentials(int pin) { this.pin = pin; }
    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
}

class Expense implements Serializable {
    private double amount;
    private String category;
    private String timestamp;

    public Expense(double amount, String category) {
        this.amount = amount;
        this.category = category;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public double getAmount() { return amount; }
    @Override
    public String toString() {
        return String.format("%-18s | %-12s | %.2f EGP", timestamp, category, amount);
    }
}

class BudgetCycle implements Serializable {
    private double totalAllowance;
    private LocalDateTime endDate;
    private double remainingBalance;
    private List<Expense> expenseHistory = new ArrayList<>();

    public BudgetCycle(double totalAllowance, int days) {
        this.totalAllowance = totalAllowance;
        this.remainingBalance = totalAllowance;
        this.endDate = LocalDateTime.now().plusDays(days);
    }

    public int getRemainingDays() { 
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
        return (days <= 0) ? 1 : (int) days + 1; 
    }
    
    public void deductAmount(double amount) { this.remainingBalance -= amount; }
    public double getRemainingBalance() { return remainingBalance; }
    public double getTotalAllowance() { return totalAllowance; } 
    public void addExpenseToHistory(Expense e) { this.expenseHistory.add(e); }
    public List<Expense> getHistory() { return expenseHistory; }
}