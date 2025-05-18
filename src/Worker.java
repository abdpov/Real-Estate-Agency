import java.util.*;
import java.sql.*;

public class Worker extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:real_state_agency.db";
    public void showMenu(){
        while(true) {
            System.out.println("1. Показать список порученных мне дел");
            System.out.println("2. Показать список завершенных указаний");
            System.out.println("3. Показать список дел над, которым я работаю");
            System.out.println("4. Показать зарплату");
            System.out.println("5. Выход");
            int option = scanner.nextInt();

            if(option == 1){
                viewMyTasks(Main.currentUser);
            } else if (option == 2) {
                markTaskAsFinished();
            } else if (option == 3) {
                viewPActiveTasks(Main.currentUser);
            } else if (option == 4) {
                viewWorkerSalary();
            } else if (option == 5) {
                break;
            } else {
                System.out.println("Недопустимое значение");
            }
        }
    }

    private static void viewMyTasks(String username) {
        scanner.nextLine();
        String getTasksSQL = "SELECT task, status FROM tasks WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, username);
            ResultSet taskRs = getTasksStmt.executeQuery();

            System.out.println("\n📝 Ваши дела:");
            boolean hasTasks = false;
            while (taskRs.next()) {
                String task = taskRs.getString("дела");
                String status = taskRs.getString("статус");
                System.out.println("- " + task + " [" + status + "]");
                hasTasks = true;
            }

            if (!hasTasks) {
                System.out.println("📭 У вас нет назначенных задач.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void markTaskAsFinished() {
        String getTasksSQL = "SELECT id, task, status FROM tasks WHERE username = ?";
        String updateTaskSQL = "UPDATE tasks SET status = 'finished' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, Main.currentUser);
            ResultSet rs = getTasksStmt.executeQuery();

            List<Integer> taskIds = new ArrayList<>();
            System.out.println("\n📋 Ваши задачи:");
            int index = 1;
            while (rs.next()) {
                int taskId = rs.getInt("id");
                String task = rs.getString("задачи");
                String status = rs.getString("статус");
                System.out.println(index + ". " + task + " [" + status + "]");
                taskIds.add(taskId);
                index++;
            }

            if (taskIds.isEmpty()) {
                System.out.println("📭 Нет назначенных задач.");
                return;
            }

            System.out.print("Введите номер задачи, которую нужно отметить как выполненную.: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice < 1 || choice > taskIds.size()) {
                System.out.println("❌ Неверный выбор.");
                return;
            }

            int selectedTaskId = taskIds.get(choice - 1);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateTaskSQL)) {
                updateStmt.setInt(1, selectedTaskId);
                updateStmt.executeUpdate();
                System.out.println("✅ Задача отмечена как завершенная!");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка обновления задачи: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("❌ Пожалуйста, введите действительный номер.");
            scanner.nextLine(); // clear invalid input
        }
    }
    private static void viewPActiveTasks(String username) {
        String getTasksSQL = "SELECT task FROM tasks WHERE username = ? AND status = 'assigned'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getTasksStmt = conn.prepareStatement(getTasksSQL)) {

            getTasksStmt.setString(1, username);
            ResultSet taskRs = getTasksStmt.executeQuery();

            System.out.println("\n📌 Ваши активные задачи:");
            boolean hasTasks = false;
            while (taskRs.next()) {
                String task = taskRs.getString("задача");
                System.out.println("- " + task);
                hasTasks = true;
            }

            if (!hasTasks) {
                System.out.println("✅ У вас нет активных задач!");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при извлечении активных задач: " + e.getMessage());
        }
    }
    private static void viewWorkerSalary() {
        scanner.nextLine(); // clear leftover newline


        String username = Main.currentUser;

        String sql = "SELECT role, salary FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("роль");
                double salary = rs.getDouble("зарплата");

                if (!role.equalsIgnoreCase("сотрудник")) {
                    System.out.println("❌ Этот пользователь не является сотрудником");
                    return;
                }

                System.out.printf("💰 ваша зарплата: %.2f\n", salary);
            } else {
                System.out.println("❌ пользователь не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении зарплаты: " + e.getMessage());
        }
    }
}
