import java.sql.*;
import java.util.*;

public class Marketing extends RoleMenu {
    private static final String DB_URL = "jdbc:sqlite:real_state_agency.db";
    Scanner scanner = new Scanner(System.in);
    public void showMenu() {
        while (true){
            System.out.println("1. Show coverage");
            System.out.println("2. Show marketing platforms");
            System.out.println("3. Show spent budget on a platform");
            System.out.println("4. Show total budget");
            System.out.println("5. Spend budget on marketing");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int option = scanner.nextInt();
            if(option == 1){
                showCoverageByRegion();
            } else if(option == 2){
                showMarketingPlatforms();
            } else if(option == 3){
                while(true){
                    System.out.println("\n--- Choose platform ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;
                    showBudgetSpent(choice);
                }
            } else if(option == 4){
                System.out.println(getMarketingBudget());
            } else if(option == 5){
                while (true) {
                    System.out.println("\n--- Choose platform ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;

                    System.out.print("Enter amount to spend: ");
                    int amount = scanner.nextInt();

                    spendOnPromotion(choice, amount);
                }
            } else if(option == 6){
                break;
            } else {
                System.out.println("Invalid option");
            }
        }
    }
    protected void showMarketingPlatforms() {
        System.out.println("\n--- Marketing Platforms ---");

        String query = "SELECT name, user_count FROM MarketingPlatforms";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                int userCount = rs.getInt("user_count");

                System.out.println("Platform: " + name);
                System.out.println("Number of subscribers: " + userCount);
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error loading marketing platforms: " + e.getMessage());
        }

        System.out.println("0. Back");
    }

    public static void showBudgetSpent(int platformId) {
        String sql = "SELECT name, budget_spent FROM MarketingPlatforms WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, platformId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double budgetSpent = rs.getDouble("budget_spent");

                System.out.println("\n--- Platform Budget Info ---");
                System.out.println("Platform: " + name);
                System.out.println("Budget spent: " + budgetSpent);
            } else {
                System.out.println("❌ Platform not found with ID: " + platformId);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching platform budget: " + e.getMessage());
        }
    }

    // Get current marketing budget
    public static double getMarketingBudget() {
        String sql = "SELECT total_budget FROM marketing_budget LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_budget");
            }
        } catch (SQLException e) {
            System.out.println("Error reading marketing budget: " + e.getMessage());
        }
        return 0;
    }

    // Spend part of the budget
    private static void spendOnPromotion(int platformId, double amount) {
        double currentBudget = getMarketingBudget();

        if (amount > currentBudget) {
            System.out.println("❌ Not enough marketing budget. Current budget: " + currentBudget);
            return;
        }

        double newBudget = currentBudget - amount;

        String getPlatformSQL = "SELECT name FROM MarketingPlatforms WHERE id = ?";
        String updateMainBudgetSQL = "UPDATE marketing_budget SET total_budget = ? WHERE id = 1";
        String updatePlatformBudgetSQL = "UPDATE MarketingPlatforms SET budget_spent = budget_spent + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false); // Start transaction

            try (
                    PreparedStatement getPlatformStmt = conn.prepareStatement(getPlatformSQL);
                    PreparedStatement updateMainBudgetStmt = conn.prepareStatement(updateMainBudgetSQL);
                    PreparedStatement updatePlatformBudgetStmt = conn.prepareStatement(updatePlatformBudgetSQL)
            ) {
                // Get platform name
                getPlatformStmt.setInt(1, platformId);
                ResultSet rs = getPlatformStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ Platform not found with ID: " + platformId);
                    return;
                }

                String platformName = rs.getString("name");

                // Update total marketing budget
                updateMainBudgetStmt.setDouble(1, newBudget);
                updateMainBudgetStmt.executeUpdate();

                // Update platform-specific budget
                updatePlatformBudgetStmt.setDouble(1, amount);
                updatePlatformBudgetStmt.setInt(2, platformId);
                updatePlatformBudgetStmt.executeUpdate();

                conn.commit();
                System.out.println("✅ Spent " + amount + " on " + platformName + ". New budget: " + newBudget);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("❌ Error during transaction: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true); // Reset autocommit
            }

        } catch (SQLException e) {
            System.out.println("❌ Error updating budget: " + e.getMessage());
        }
    }
}
