

 // CoreTech Innovations Services Manager (Bonus Version)



import java.util.ArrayList;
import java.util.Scanner;

public class Task3_MuhammadIqbal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<String> serviceNames = new ArrayList<>();
        ArrayList<Double> servicePrices = new ArrayList<>();

        System.out.println("=== CoreTech Innovations - Service Entry ===");
        System.out.println("Type 'stop' to finish adding services.\n");

        // Input loop until user types stop
        while (true) {
            System.out.print("Enter name of service " + (serviceNames.size() + 1) + ": ");
            String serviceName = scanner.nextLine().trim();

            if (serviceName.equalsIgnoreCase("stop")) {
                break; // exit loop
            }

            // Error handling for price input
            double price;
            while (true) {
                try {
                    System.out.print("Enter price of service: Rs. ");
                    price = Double.parseDouble(scanner.nextLine().trim());
                    if (price < 0) {
                        System.out.println(" Price cannot be negative. Please enter again.");
                        continue;
                    }
                    break; // valid price entered
                } catch (NumberFormatException e) {
                    System.out.println(" Invalid input! Please enter a numeric value for price.");
                }
            }

            serviceNames.add(serviceName);
            servicePrices.add(price);
            System.out.println("Service added successfully!\n");
        }

        // Display all services
        if (serviceNames.isEmpty()) {
            System.out.println("\nNo services entered.");
        } else {
            System.out.println("\n=== CoreTech Innovations Services ===");
            for (int i = 0; i < serviceNames.size(); i++) {
                System.out.println((i + 1) + ". " + serviceNames.get(i) + " - Rs. " + servicePrices.get(i));
            }
        }

        // Search for a service
        if (!serviceNames.isEmpty()) {
            System.out.print("\nSearch for a service: ");
            String searchQuery = scanner.nextLine().trim();

            boolean found = false;
            for (int i = 0; i < serviceNames.size(); i++) {
                if (serviceNames.get(i).equalsIgnoreCase(searchQuery)) {
                    System.out.println(" Service Found: " + serviceNames.get(i) + " - Rs. " + servicePrices.get(i));
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println(" Service not available.");
            }
        }

        scanner.close();
    }
}
