import java.sql.*;
import java.util.*;

public class Sales extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:real_state_agency.db";

    public void showMenu(){
        while (true){
            System.out.println("1. Показать всех клиентов");
            System.out.println("2. Show houses for sale");
            System.out.println("3. Show sold houses");
            System.out.println("4. Show most expensive house");
            System.out.println("5. Show cheapest house");
            System.out.println("6. Exit");
            int option = scanner.nextInt();

            if(option == 1){
                System.out.println("Total amount of clients is: 1120");
            } else if (option == 2) {
                viewAvailableHouses();
            } else if (option == 3) {
                viewSoldHouses();
            } else if (option == 4) {
                getMostExpensiveHouse();
            } else if (option == 5) {
                getCheapestHouse();
            } else if (option == 6) {
                break;
            }  else {
                System.out.println("Invalid option");
            }
        }
    }
    private static void viewAvailableHouses() {
        String sql = "SELECT id, price, address FROM houses WHERE status = 'available'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🏡 Available Houses:");
            boolean found = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("price");
                String address = rs.getString("address");

                System.out.println("- ID: " + id + ", $" + price + ", " + address);
                found = true;
            }

            if (!found) {
                System.out.println("📭 No available houses found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving available houses: " + e.getMessage());
        }
    }
    private static void viewSoldHouses() {
        String sql = "SELECT id, price, address FROM houses WHERE status = 'sold'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🏠 Sold Houses:");
            boolean found = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("price");
                String address = rs.getString("address");

                System.out.println("- ID: " + id + ", $" + price + ", " + address);
                found = true;
            }

            if (!found) {
                System.out.println("📭 No sold houses found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving sold houses: " + e.getMessage());
        }
    }
    private static void getMostExpensiveHouse() {
        String sql = "SELECT id, price, address, status FROM houses ORDER BY price DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n🤑 Most Expensive House:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Price: $" + rs.getDouble("price"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Status: " + rs.getString("status"));
            } else {
                System.out.println("📭 No houses found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving most expensive house: " + e.getMessage());
        }
    }
    private static void getCheapestHouse() {
        String sql = "SELECT id, price, address, status FROM houses ORDER BY price ASC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n💸 Cheapest House:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Price: $" + rs.getDouble("price"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Status: " + rs.getString("status"));
            } else {
                System.out.println("📭 No houses found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving cheapest house: " + e.getMessage());
        }
    }

}
