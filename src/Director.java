import java.util.*;
import  java.sql.*;

public class Director extends RoleMenu{
    static Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:sqlite:real_estate_agency.db";
    public void showMenu(){
        while (true){
            System.out.println("1. Показать список всех зон покрытия ");
            System.out.println("2. Показать список категорий бюджета");
            System.out.println("3. Показать выделенный бюджет для определенной категории мест для маркетинга");
            System.out.println("4. Показать текущие средства для маркетинга");
            System.out.println("5. Показать общий бюджет необходимый для зарплаты");
            System.out.println("6. Повысить зарплату сотруднику");
            System.out.println("7. Понизить зарплату сотруднику");
            System.out.println("8. Показать список оборудований для строительства объектов");
            System.out.println("9. Выход");
            int option = scanner.nextInt();

            if (option == 1){
                showCoverageByRegion();
            } else if (option == 2){
                System.out.println("бюджет для маркетинга\nбюджет для заработной платы");
            } else if (option == 3){
                while(true){
                    System.out.println("\n--- Выберите платформу ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;
                    Marketing.showBudgetSpent(choice);
                }
            } else if (option == 4){
                System.out.println(Marketing.getMarketingBudget());
            } else if (option == 5){
                showTotalSalaryBudget();
            } else if (option == 6){
                increaseSalary();
            } else if (option == 7){
                decreaseSalary();
            } else if (option == 8){
                String equipment = """
                                 Бульдозер
                                 Снегоочиститель
                                 Снегоочиститель
                                 Трелевочный трактор
                                 Трактор
                                 Гусеничный трактор
                                 Локомотив
                                 Артиллерийский тягач
                                 Гусеничный транспортер
                        """;
                System.out.println(equipment);
            } else if (option == 9){
                break;
            } else {
                System.out.println("Недопустимое значение");
            }
        }
    }
    private static void showTotalSalaryBudget() {
        String sql = "SELECT SUM(salary) AS total_salary FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double total = rs.getDouble("общая зп");
                System.out.printf("💼 общий бюджеь зп: %.2f\n", total);
            } else {
                System.out.println("Данные о зарплате не найдены.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка расчета общей зарплаты: " + e.getMessage());
        }
    }
    private static void increaseSalary() {
        scanner.nextLine();

        System.out.print("Введите имя пользователя, чтобы увеличить зарплату: ");
        String username = scanner.nextLine();

        System.out.print("Введите сумму для увеличения (например, 2000): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("❌ Сумма должна быть положительной..");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверная суммаt.");
            return;
        }

        String getSalarySQL = "SELECT salary FROM users WHERE username = ?";
        String updateSalarySQL = "UPDATE users SET salary = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getStmt = conn.prepareStatement(getSalarySQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSalarySQL)) {

            getStmt.setString(1, username);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                double currentSalary = rs.getDouble("зп");
                double newSalary = currentSalary + amount;

                updateStmt.setDouble(1, newSalary);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                System.out.printf("✅ Зарплата выросла с %.2f to %.2f%n", currentSalary, newSalary);
            } else {
                System.out.println("❌ Пользователь не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка обновления зарплаты: " + e.getMessage());
        }
    }
    private static void decreaseSalary() {
        scanner.nextLine();

        System.out.print("Введите имя пользователя, чтобы уменьшить зарплату: ");
        String username = scanner.nextLine();

        System.out.print("Введите сумму для уменьшения (например, 1500): ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("❌ Сумма должна быть положительной..");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Неверная сумма.");
            return;
        }

        String getSalarySQL = "SELECT salary FROM users WHERE username = ?";
        String updateSalarySQL = "UPDATE users SET salary = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement getStmt = conn.prepareStatement(getSalarySQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSalarySQL)) {

            getStmt.setString(1, username);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                double currentSalary = rs.getDouble("зп");
                double newSalary = currentSalary - amount;

                if (newSalary < 0) {
                    System.out.println("❌ Зарплата не может быть ниже 0.");
                    return;
                }

                updateStmt.setDouble(1, newSalary);
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                System.out.printf("✅ Заработная плата снизилась с %.2f to %.2f%n", currentSalary, newSalary);
            } else {
                System.out.println("❌ Пользователь не найден.");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка обновления зарплаты: " + e.getMessage());
        }
    }
}
