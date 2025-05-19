import java.util.*;
import java.sql.*;

public class Manager extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:real_estate_agency.db";
    public void showMenu() {
        while (true){
            System.out.println("1. Перечислите всех рабочих");
            System.out.println("2. Назначение задач");
            System.out.println("3. Показать назначенные задачи");
            System.out.println("4. Покажите зону охвата");
            System.out.println("5. Выход");
            int option = scanner.nextInt();

            if(option == 1){
                showAllWorkers();
            } else if(option == 2){
                assignTask();
            } else if(option == 3){
                viewWorkerTasks();
            } else if(option == 4){
                showCoverageByRegion();
            } else if(option == 5){
                break;

            } else {
                System.out.println("Неверный вариант");
            }
        }
    }
    private static void showAllWorkers() {
        String sql = "SELECT username FROM users WHERE role = 'worker'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Все работники ---");
            while (rs.next()) {
                String username = rs.getString("username");
                System.out.println("👤 " + username);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при извлечении пользователей: " + e.getMessage());
        }
    }

    private static void assignTask() {
        scanner.nextLine();

        System.out.print("Введите имя пользователя, которому будет назначена задача: ");
        String username = scanner.nextLine();

        System.out.print("Введите описание задачи (например, «Завершить отчет»): ");
        String task = scanner.nextLine();

        String getUserSQL = "SELECT id, role FROM users WHERE username = ?";
        String insertTaskSQL = "INSERT INTO tasks (username, task) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getUserStmt = conn.prepareStatement(getUserSQL)) {

            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");

                if (!role.equalsIgnoreCase("worker")) {
                    System.out.println("❌ Невозможно назначать задачи нерабочим ролям (например, " + role + ").");
                    return;
                }

                try (PreparedStatement insertStmt = conn.prepareStatement(insertTaskSQL)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, task);
                    insertStmt.executeUpdate();

                    System.out.println("✅ Task assigned to worker: " + username);
                }

            } else {
                System.out.println("❌ Задача, назначенная работнику.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка назначения задачи: " + e.getMessage());
        }
    }
    private static void viewWorkerTasks() {
        scanner.nextLine(); // Clear leftover newline if needed

        System.out.print("Enter the worker's username: ");
        String username = scanner.nextLine();

        String getUserSQL = "SELECT role FROM users WHERE username = ?";
        String getTasksSQL = "SELECT task, status FROM tasks WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getUserStmt = conn.prepareStatement(getUserSQL)) {

            getUserStmt.setString(1, username);
            ResultSet userRs = getUserStmt.executeQuery();

            if (userRs.next()) {
                String role = userRs.getString("role");

                if (!role.equalsIgnoreCase("worker")) {
                    System.out.println("❌ That user is not a worker.");
                    return;
                }

                try (PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {
                    getTasksStmt.setString(1, username);
                    ResultSet taskRs = getTasksStmt.executeQuery();

                    System.out.println("\n📋 Задачи, возложенные на " + username + ":");
                    boolean hasTasks = false;
                    while (taskRs.next()) {
                        String task = taskRs.getString("task");
                        String status = taskRs.getString("status");
                        System.out.println("- " + task + (status != null ? " [" + status + "]" : ""));
                        hasTasks = true;
                    }

                    if (!hasTasks) {
                        System.out.println("📭 Нет назначенных задач.");
                    }
                }

            } else {
                System.out.println("❌ Пользователь не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении задач: " + e.getMessage());
        }
    }
}
