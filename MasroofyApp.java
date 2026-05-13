import java.util.Scanner;
import java.util.List;

public class MasroofyApp {

    public static double getSafeDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                double val = sc.nextDouble();
                if (val > 0) return val;
                System.out.println("Error: Positive number only.");
            } else {
                System.out.println("Error: Invalid input.");
                sc.next();
            }
        }
    }

    public static int getSafeInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int val = sc.nextInt();
                if (val > 0) return val;
                System.out.println("Error: Positive integer only.");
            } else {
                System.out.println("Error: Whole number only.");
                sc.next();
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SQLiteManager db = new SQLiteManager();
        NotificationService ns = new NotificationService();
        DashboardView dash = new DashboardView();
        SecurityService secService = new SecurityService(db);
        BudgetManager bMan = new BudgetManager(db, ns);
        ExpenseLogger logger = new ExpenseLogger(bMan, db);

        // Security Authentication
        if (!secService.hasCredentials()) {
            System.out.println("Welcome to Masroofy! Initial Setup.");
            int pin = getSafeInt(sc, "Setup 4-digit PIN: ");
            secService.setupFirstTimePin(pin);
        } else {
            System.out.print("Enter PIN: ");
            try {
                if (!secService.authenticate(sc.nextInt())) {
                    System.out.println("Access Denied.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Access Denied.");
                return;
            }
        }

        while (true) {
            if (bMan.getCurrentCycle() == null) {
                System.out.println("\n--- Setup New Budget ---");
                double a = getSafeDouble(sc, "Total Budget: ");
                int d = getSafeInt(sc, "Number of Days: ");
                bMan.setupCycle(a, d);
            }

            System.out.println("\n1.Add Expense | 2.Dashboard | 3.History | 4.Change PIN | 5.Reset | 6.Exit");
            System.out.print("Choice: ");
            if (!sc.hasNextInt()) { sc.next(); continue; }
            int ch = sc.nextInt();

            if (ch == 1) {
                double amt = getSafeDouble(sc, "Amount: ");
                System.out.print("Category: "); String cat = sc.next();
                logger.logExpense(amt, cat, dash);
            } else if (ch == 2) {
                BudgetCycle cy = bMan.getCurrentCycle();
                dash.renderDashboard(cy.getRemainingBalance(), cy.getRemainingBalance() / cy.getRemainingDays());
            } else if (ch == 3) {
                List<Expense> history = bMan.getCurrentCycle().getHistory();
                if (history.isEmpty()) System.out.println("No records.");
                for (Expense e : history) System.out.println(e);
            } else if (ch == 4) {
                int old = getSafeInt(sc, "Verify Old PIN: ");
                if (secService.authenticate(old)) {
                    int next = getSafeInt(sc, "New PIN: ");
                    secService.updatePin(next);
                    System.out.println("PIN Updated.");
                }
            } else if (ch == 5) {
                bMan.reset();
                System.out.println("Budget Reset.");
            } else if (ch == 6) break;
        }
        sc.close();
    }
}