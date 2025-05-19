import java.sql.*;
import java.util.*;

public class Sales extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:real_estate_agency.db";
    // метод показывающий менюшку
    public void showMenu(){
        while (true){
            System.out.println("1. Показать всех клиентов");
            System.out.println("2. Показать дома на продажу");
            System.out.println("3. Показать проданные дома");
            System.out.println("4. Показать самый дорогой дом");
            System.out.println("5. Показать самый дешевый дом");
            System.out.println("6. Выход");
            int option = scanner.nextInt();

            if(option == 1){
                System.out.println("Общее количество клиентов: 1120");
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
                System.out.println("Неверная опция");
            }
        }
    }
    // метод показывающйи доступнве дома
    private static void viewAvailableHouses() {
        String sql = "SELECT id, price, address FROM houses WHERE status = 'available'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🏡 Доступные дома:");
            boolean found = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("price");
                String address = rs.getString("address");

                System.out.println("- ID: " + id + ", $" + price + ", " + address);
                found = true;
            }

            if (!found) {
                System.out.println("📭 Не найдено доступных домов.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении доступных домов: " + e.getMessage());
        }
    }
    //метод показывающий проданные дома
    private static void viewSoldHouses() {
        String sql = "SELECT id, price, address FROM houses WHERE status = 'sold'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🏠 Проданные дома:");
            boolean found = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                double price = rs.getDouble("price");
                String address = rs.getString("address");

                System.out.println("- ID: " + id + ", $" + price + ", " + address);
                found = true;
            }

            if (!found) {
                System.out.println("📭 Не найдены проданные дома.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении проданных домов: " + e.getMessage());
        }
    }
    //метод показывающий самые дорогие дома
    private static void getMostExpensiveHouse() {
        String sql = "SELECT id, price, address, status FROM houses ORDER BY price DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n🤑 Самый дорогой дом:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Цена: $" + rs.getDouble("price"));
                System.out.println("Адрес: " + rs.getString("address"));
                System.out.println("SСтатус: " + rs.getString("status"));
            } else {
                System.out.println("📭 Дома не найдены.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении самого дорогого дома: " + e.getMessage());
        }
    }
    //метод показывающий самые дорогие дома
    private static void getCheapestHouse() {
        String sql = "SELECT id, price, address, status FROM houses ORDER BY price ASC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("\n💸 Самый дешевый дом:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Цена: $" + rs.getDouble("price"));
                System.out.println("Адрес: " + rs.getString("address"));
                System.out.println("Статус: " + rs.getString("status"));
            } else {
                System.out.println("📭 Не найдено домов.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении самого дешевого дома: " + e.getMessage());
        }
    }

}
