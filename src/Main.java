import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:real_estate_agency.db";
    private static final String[] ALLOWED_ROLES = {
            "manager", "worker", "director", "sales manager", "marketing"
    };
    private static String currentRole = null;
    public static String currentUser = null;
    public static int loggedInUserId = -1;
    // метод для входа в систему
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        createUsersTable();

        System.out.println("\n1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("0. Выход");
        System.out.print("Выберите опцию: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (option == 1) {
            System.out.print("Введите имя пользователя: ");
            String username = scanner.nextLine();

            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            // Step 2: Validate role input
            String role;
            while (true) {
                System.out.print("Выберите должность (Manager\\Worker\\Director\\Sales manager\\Marketing): ");
                role = scanner.nextLine().toLowerCase();

                boolean valid = false;
                for (String allowed : ALLOWED_ROLES) {
                    if (allowed.equals(role)) {
                        valid = true;
                        break;
                    }
                }

                if (valid) break;
                System.out.println("Неверная должность, попробуйте еще.");
            }

            // Register with validated role
            registerUser(username, password, role);
        }
        else if (option == 2) {
            System.out.print("Ведите тип аккаунта: ");
            String acc_type = scanner.nextLine();
            if(Arrays.toString(ALLOWED_ROLES).contains(acc_type)){
                System.out.print("Введите имя пользователя: ");
                String username = scanner.nextLine();
                System.out.print("Введите праорль: ");
                String password = scanner.nextLine();
                loginUser(username, password);

                RoleMenu menu = Menus.getMenu(currentRole);
                if (menu != null) {
                    menu.showMenu();
                } else {
                    System.out.println("Неизвестная должность. Выход.");
                }
            } else {
                System.out.println("Неправильный тип аккаунта, попробуйте еще раз!");
                return;
            }


        } else if (option == 0) {
            return;
        }

        scanner.close();
    }

    private static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL" +
                "role TEXT NOT NULL" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Ошибка создания таблицы: " + e.getMessage());
        }
    }

    private static void registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toLowerCase());
            pstmt.executeUpdate();

            System.out.println("Пользователь успешно зарегистрирован с должностью: " + role);
        } catch (SQLException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    private static void loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            currentUser = username;

            if (rs.next()) {
                String role = rs.getString("role");
                currentRole = role;
                loggedInUserId = rs.getInt("id");
                System.out.println("Вход успешный. Добро пожаловать " + username + " (" + role + ")");
            } else {
                System.out.println("Неверный логин.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка входа: " + e.getMessage());
        }
    }
}
/*
1 connect database to main class
2 make user login password system
3 make the first check acc_type eg manager worker director...
4 after successful sign in open menu with only exit function (for now)
5 based on chosen role open menu
6 write menus logics. start with manager first


? clean code

coverage option used by marketing doesnt change percentage
*/