

import java.util.ArrayList;
import java.util.Scanner;

/**
 * BankAccount class
 * Represents a generic bank account with basic operations.
 */
class BankAccount {
    protected String accountNumber;   // Unique account number
    protected String accountHolder;   // Name of account holder
    protected double balance;         // Current balance

    // Constructor to initialize account details
    public BankAccount(String accountNumber, String accountHolder, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    // Deposit money into account
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        balance += amount;
        System.out.println("Deposit successful. New balance: Rs. " + balance);
    }

    // Withdraw money from account
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        if (amount > balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        balance -= amount;
        System.out.println("Withdrawal successful. New balance: Rs. " + balance);
    }

    // Transfer money to another account
    public void transfer(BankAccount target, double amount) {
        if (amount <= 0) {
            System.out.println("Transfer amount must be positive.");
            return;
        }
        if (amount > balance) {
            System.out.println("Insufficient balance for transfer.");
            return;
        }
        balance -= amount;
        target.balance += amount;
        System.out.println("Transfer successful. New balance: Rs. " + balance);
    }

    // Display account details
    public void displayDetails() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Balance: Rs. " + balance);
    }
}

/**
 * SavingsAccount class
 * Extends BankAccount and adds interest calculation functionality.
 */
class SavingsAccount extends BankAccount {
    private double interestRate; // Annual interest rate in percentage

    // Constructor
    public SavingsAccount(String accountNumber, String accountHolder, double balance, double interestRate) {
        super(accountNumber, accountHolder, balance);
        this.interestRate = interestRate;
    }

    // Calculate interest for current balance
    public void calculateInterest() {
        double interest = balance * (interestRate / 100);
        System.out.println("Interest for current balance at " + interestRate + "%: Rs. " + interest);
    }
}

/**
 * Main BankingSystemBonus class
 * Handles user interaction and manages account operations.
 */
public class BankingSystem {
    private static ArrayList<BankAccount> accounts = new ArrayList<>(); // Stores all accounts
    private static Scanner scanner = new Scanner(System.in);            // For user input

    public static void main(String[] args) {
        int choice;
        do {
            // Display main menu
            System.out.println("\n=== Banking System Menu ===");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Display Account Details");
            System.out.println("6. Calculate Interest (Savings Account)");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            // Validate menu input
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // consume leftover newline

            // Handle user choice
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    depositMoney();
                    break;
                case 3:
                    withdrawMoney();
                    break;
                case 4:
                    transferMoney();
                    break;
                case 5:
                    displayAccount();
                    break;
                case 6:
                    calculateInterest();
                    break;
                case 7:
                    System.out.println("Exiting Banking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please select between 1-7.");
            }
        } while (choice != 7); // Loop until user chooses to exit
    }

    // Create a new account
    private static void createAccount() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        System.out.print("Enter Account Holder Name: ");
        String accHolder = scanner.nextLine().trim();

        double initialBalance = getValidAmount("Enter Initial Balance: Rs. ");

        // Ask if it's a savings account
        System.out.print("Is this a savings account? (yes/no): ");
        String type = scanner.nextLine().trim().toLowerCase();

        if (type.equals("yes")) {
            double rate = getValidAmount("Enter Interest Rate (%): ");
            accounts.add(new SavingsAccount(accNum, accHolder, initialBalance, rate));
        } else {
            accounts.add(new BankAccount(accNum, accHolder, initialBalance));
        }

        System.out.println("Account created successfully!");
    }

    // Deposit money into an existing account
    private static void depositMoney() {
        BankAccount account = findAccount();
        if (account != null) {
            double amount = getValidAmount("Enter deposit amount: Rs. ");
            account.deposit(amount);
        }
    }

    // Withdraw money from an existing account
    private static void withdrawMoney() {
        BankAccount account = findAccount();
        if (account != null) {
            double amount = getValidAmount("Enter withdrawal amount: Rs. ");
            account.withdraw(amount);
        }
    }

    // Transfer money between accounts
    private static void transferMoney() {
        System.out.println("Sender Account:");
        BankAccount sender = findAccount();
        if (sender != null) {
            System.out.println("Receiver Account:");
            BankAccount receiver = findAccount();
            if (receiver != null && sender != receiver) {
                double amount = getValidAmount("Enter transfer amount: Rs. ");
                sender.transfer(receiver, amount);
            } else if (sender == receiver) {
                System.out.println("Cannot transfer to the same account.");
            }
        }
    }

    // Display details of an existing account
    private static void displayAccount() {
        BankAccount account = findAccount();
        if (account != null) {
            account.displayDetails();
        }
    }

    // Calculate interest for savings accounts
    private static void calculateInterest() {
        BankAccount account = findAccount();
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).calculateInterest();
        } else if (account != null) {
            System.out.println("This is not a savings account. Interest calculation not available.");
        }
    }

    // Search for an account by account number
    private static BankAccount findAccount() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        for (BankAccount acc : accounts) {
            if (acc.accountNumber.equals(accNum)) {
                return acc;
            }
        }
        System.out.println("Account not found.");
        return null;
    }

    // Validate and return a valid amount from user input
    private static double getValidAmount(String prompt) {
        double amount;
        while (true) {
            try {
                System.out.print(prompt);
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount < 0) {
                    System.out.println("Amount cannot be negative. Try again.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a numeric value.");
            }
        }
    }
}
