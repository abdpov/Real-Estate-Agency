// метод который принимает роль пользователя, а потом возваращет его в класс RoleMenu
public class Menus {
    public static RoleMenu getMenu(String role) {
        return switch (role.toLowerCase()) {
            case "director" -> new Director();
            case "marketing" -> new Marketing();
            case "manager" -> new Manager();
            case "worker" -> new Worker();
            case "sales manager" -> new Sales();
            default -> null;
        };
    }
}

