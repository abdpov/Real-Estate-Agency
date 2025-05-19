import java.sql.*;
import java.util.*;

public class Marketing extends RoleMenu {
    private static final String DB_URL = "jdbc:sqlite:real_estate_agency.db";
    Scanner scanner = new Scanner(System.in);
    // метод показывающи йсменюшку для маркетолога
    public void showMenu() {
        while (true){
            System.out.println("1. Показать зону");
            System.out.println("2. Показать маркетинговые платформы");
            System.out.println("3. Показать потраченный бюджет на платформе");
            System.out.println("4. Показать общий бюджет");
            System.out.println("5. Расходы на маркетинг");
            System.out.println("6. Выход");
            System.out.print("Выберите опцию: ");
            int option = scanner.nextInt();
            if(option == 1){
                showCoverageByRegion();
            } else if(option == 2){
                showMarketingPlatforms();
            } else if(option == 3){
                while(true){
                    System.out.println("\n--- Выберите платформу ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;
                    showBudgetSpent(choice);
                }
            } else if(option == 4){
                System.out.println(getMarketingBudget());
            } else if(option == 5){
                while (true) {
                    System.out.println("\n--- выберите платформу ---\n1. Facebook\n2. Instagram\n3. YouTube\n4. Telegram\n0. Back");
                    int choice = scanner.nextInt();
                    if (choice == 0) break;

                    System.out.print("Введите сумму, которую хотите потратить: ");
                    int amount = scanner.nextInt();

                    spendOnPromotion(choice, amount);
                }
            } else if(option == 6){
                break;
            } else {
                System.out.println("Неверная опция");
            }
        }
    }
    // метод показывающий список маркетинговых платформ и информации
    // о количестве пользователей на каждой из них
    protected void showMarketingPlatforms() {
        System.out.println("\n--- Маркетинговые платформы ---");

        String query = "SELECT name, user_count FROM MarketingPlatforms";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                int userCount = rs.getInt("user_count");

                System.out.println("Платформа: " + name);
                System.out.println("Number of subscribers: " + userCount);
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Ошибка загрузки маркетинговых платформ: " + e.getMessage());
        }

        System.out.println("0. назад");
    }
    // метод для отображения инфы о потраченном бюджете на маркетинговой платформе по ID
    public static void showBudgetSpent(int platformId) {
        String sql = "SELECT name, budget_spent FROM MarketingPlatforms WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, platformId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double budgetSpent = rs.getDouble("budget_spent");

                System.out.println("\n--- Информация о бюджете платформы ---");
                System.out.println("Платформа: " + name);
                System.out.println("Бюджет израсходован: " + budgetSpent);
            } else {
                System.out.println("❌ Platform not found with ID: " + platformId);
            }

        } catch (SQLException e) {
            System.out.println("❌ Ошибка при получении бюджета платформы: " + e.getMessage());
        }
    }

    // метод для получения общего маркетингового бюджета из таблицы marketing_budget в базе данных
    public static double getMarketingBudget() {
        String sql = "SELECT total_budget FROM marketing_budget LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_budget");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка чтения маркетингового бюджета: " + e.getMessage());
        }
        return 0;
    }

    // мемтод показывающий траты бюджета на продвижение конкретной маркетинговой платформы
    private static void spendOnPromotion(int platformId, double amount) {
        double currentBudget = getMarketingBudget();

        if (amount > currentBudget) {
            System.out.println("❌ Недостаточно маркетингового бюджета. Текущий бюджет: " + currentBudget);
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
                    System.out.println("❌ Платформа с идентификатором не найдена: " + platformId);
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
                System.out.println("✅ Вложите " + amount + " на " + platformName + ". бюджет: " + newBudget);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("❌ Ошибка во время транзакции: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true); // Reset autocommit
            }

        } catch (SQLException e) {
            System.out.println("❌ Ошибка обновления бюджета.: " + e.getMessage());
        }
    }
}
