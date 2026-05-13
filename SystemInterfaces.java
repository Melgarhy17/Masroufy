import java.io.*;

class DashboardView {
    public void showNotification(String message) {
        System.out.println("\n[ALERT] " + message);
    }
    
    public void renderDashboard(double balance, double safeLimit) {
        System.out.println("\n--- Dashboard Status ---");
        System.out.println("Remaining Balance: " + String.format("%.2f", balance));
        System.out.println("Safe Daily Limit : " + String.format("%.2f", safeLimit));
    }
}

class SQLiteManager {
    private final String DB_FILE = "masroofy_main.dat";
    private final String SEC_FILE = "masroofy_sec.dat";

    public BudgetCycle fetchCurrentCycle() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB_FILE))) {
            return (BudgetCycle) ois.readObject();
        } catch (Exception e) { return null; }
    }

    public void saveCycle(BudgetCycle cycle) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(cycle);
        } catch (IOException e) { }
    }

    public void saveCredentials(UserCredentials creds) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SEC_FILE))) {
            oos.writeObject(creds);
        } catch (IOException e) { }
    }

    public UserCredentials fetchCredentials() {
        try (ObjectInputStream i = new ObjectInputStream(new FileInputStream(SEC_FILE))) {
            return (UserCredentials) i.readObject();
        } catch (Exception e) { return null; }
    }

    public void deleteData() { new File(DB_FILE).delete(); }
}