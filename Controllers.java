import java.util.List;

class SecurityService {
    private UserCredentials credentials;
    private SQLiteManager db;

    public SecurityService(SQLiteManager db) {
        this.db = db;
        this.credentials = db.fetchCredentials();
    }

    public boolean hasCredentials() { return credentials != null; }

    public void setupFirstTimePin(int pin) {
        this.credentials = new UserCredentials(pin);
        db.saveCredentials(this.credentials);
    }

    public boolean authenticate(int input) {
        return credentials != null && credentials.getPin() == input;
    }

    public void updatePin(int nextPin) {
        if (credentials != null) {
            credentials.setPin(nextPin);
            db.saveCredentials(credentials);
        }
    }
}

class NotificationService {
    public void checkThreshold(BudgetCycle cycle, DashboardView view) {
        double usagePercentage = (cycle.getRemainingBalance() / cycle.getTotalAllowance());
        if (usagePercentage <= 0.20) { 
            view.showNotification("Warning: You have used 80% of your budget!");
        }
    }
}

class BudgetManager {
    private BudgetCycle currentCycle;
    private SQLiteManager db;
    private NotificationService ns;

    public BudgetManager(SQLiteManager db, NotificationService ns) {
        this.db = db;
        this.ns = ns;
        this.currentCycle = db.fetchCurrentCycle();
    }

    public void setupCycle(double a, int d) {
        this.currentCycle = new BudgetCycle(a, d);
        db.saveCycle(currentCycle);
    }

    public BudgetCycle getCurrentCycle() { return currentCycle; }
    public void reset() { this.currentCycle = null; db.deleteData(); }

    public void deduct(double amt, DashboardView v) {
        if (currentCycle != null) {
            currentCycle.deductAmount(amt);
            db.saveCycle(currentCycle);
            ns.checkThreshold(currentCycle, v);
        }
    }
}

class ExpenseLogger {
    private BudgetManager bm;
    private SQLiteManager db;

    public ExpenseLogger(BudgetManager bm, SQLiteManager db) {
        this.bm = bm;
        this.db = db;
    }

    public void logExpense(double amt, String cat, DashboardView v) {
        if (amt > 0 && bm.getCurrentCycle() != null) {
            Expense e = new Expense(amt, cat);
            bm.getCurrentCycle().addExpenseToHistory(e);
            bm.deduct(amt, v);
            System.out.println(">>> Expense recorded.");
        }
    }
}