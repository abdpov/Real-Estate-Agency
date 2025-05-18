import java.util.*;

public abstract class RoleMenu {
    abstract void showMenu();


    public static void showCoverageByRegion() {
        Map<Integer, String> regionNames = new LinkedHashMap<>();
        regionNames.put(1, "Bishkek");
        regionNames.put(2, "Talas");
        regionNames.put(3, "Jalalabad");
        regionNames.put(4, "Osh");
        regionNames.put(5, "Naryn");
        regionNames.put(6, "Issyk Kul");
        regionNames.put(7, "Batken");

        Map<String, Integer> coverageMap = Map.of(
                "Bishkek", 78,
                "Talas", 62,
                "Jalalabad", 60,
                "Osh", 70,
                "Naryn", 55,
                "Issyk Kul", 64,
                "Batken", 59
        );

        Map<String, Integer> customerMap = Map.of(
                "Bishkek", 250,
                "Talas", 140,
                "Jalalabad", 180,
                "Osh", 220,
                "Naryn", 90,
                "Issyk Kul", 130,
                "Batken", 110
        );

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Client Coverage by Region ---");
            for (Map.Entry<Integer, String> entry : regionNames.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            System.out.println("0. Back");

            System.out.print("Choose a region: ");
            int choice = scanner.nextInt();

            if (choice == 0) break;

            String region = regionNames.get(choice);
            if (region != null) {
                int coverage = coverageMap.get(region);
                int customers = customerMap.get(region);

                System.out.println("\nRegion: " + region);
                System.out.println("Coverage: " + coverage + "%");
                System.out.println("Number of customers: " + customers);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
