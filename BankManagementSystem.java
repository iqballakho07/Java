import java.util.ArrayList;
import java.util.Scanner;

// Class representing a Bank Account
class BankAccount {
    private int accountNumber;
    private String accountHolderName;
    private double balance;

    // Constructor
    public BankAccount(int accountNumber, String accountHolderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
    }

    public double getBalance(){
        return balance;
    }
    // Method to deposit money
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Successfully deposited: " + amount);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    // Method to withdraw money
    public void withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("Successfully withdrawn: " + amount);
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    // Method to check balance
    public void checkBalance() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolderName);
        System.out.println("Current Balance: " + balance);
    }

    // Getter for account number
    public int getAccountNumber() {
        return accountNumber;
    }
}

// Class representing the Bank which manages multiple accounts
class Bank {
    private ArrayList<BankAccount> accounts = new ArrayList<>();

    // Method to create new account
    public void createAccount(int accountNumber, String accountHolderName, double initialBalance) {
        BankAccount newAccount = new BankAccount(accountNumber, accountHolderName, initialBalance);
        accounts.add(newAccount);
        System.out.println("Account created successfully.");
    }

    // Method to find account by account number
    public BankAccount findAccount(int accountNumber) {
        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber() == accountNumber) {
                return acc;
            }
        }
        return null; // if not found
    }

    // Method to transfer money between accounts
    public void transferMoney(int fromAccountNumber, int toAccountNumber, double amount) {
        BankAccount fromAccount = findAccount(fromAccountNumber);
        BankAccount toAccount = findAccount(toAccountNumber);

        if (fromAccount != null && toAccount != null) {
            if (amount > 0) {
                if (fromAccountNumber == toAccountNumber) {
                    System.out.println("Cannot transfer to the same account.");
                } else {
                    if (amount <= 0) {
                        System.out.println("Invalid amount.");
                    } else {
                        if (fromAccount != null && toAccount != null) {
                            if (fromAccountNumber == toAccountNumber) {
                                System.out.println("Cannot transfer to the same account.");
                            } else {
                                if (amount > 0) {
                                    if (fromAccountNumber != toAccountNumber) {
                                        if (amount <= 0) {
                                            System.out.println("Invalid amount.");
                                        } else {
                                            if (amount > 0) {
                                                if (fromAccountNumber != toAccountNumber) {
                                                    if (fromAccount != null && toAccount != null) {
                                                        if (amount > 0 && amount <= fromAccount.getBalance()) {
                                                            fromAccount.withdraw(amount);
                                                            toAccount.deposit(amount);
                                                            System.out.println("Transfer successful: " + amount);
                                                        } else {
                                                            System.out.println("Transfer failed. Insufficient funds.");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("One or both accounts not found.");
        }
    }
}

// Main class with menu
public class BankManagementSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();

        while (true) {
            try {
                System.out.println("\n===== Online Banking System =====");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Check Balance");
                System.out.println("5. Transfer Money");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter Account Number: ");
                        int accNum = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter Account Holder Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Initial Balance: ");
                        double initBalance = scanner.nextDouble();
                        bank.createAccount(accNum, name, initBalance);
                        break;

                    case 2:
                        System.out.print("Enter Account Number: ");
                        int depAccNum = scanner.nextInt();
                        System.out.print("Enter Deposit Amount: ");
                        double depAmount = scanner.nextDouble();
                        BankAccount depAccount = bank.findAccount(depAccNum);
                        if (depAccount != null) {
                            depAccount.deposit(depAmount);
                        } else {
                            System.out.println("Account not found.");
                        }
                        break;

                    case 3:
                        System.out.print("Enter Account Number: ");
                        int witAccNum = scanner.nextInt();
                        System.out.print("Enter Withdraw Amount: ");
                        double witAmount = scanner.nextDouble();
                        BankAccount witAccount = bank.findAccount(witAccNum);
                        if (witAccount != null) {
                            witAccount.withdraw(witAmount);
                        } else {
                            System.out.println("Account not found.");
                        }
                        break;

                    case 4:
                        System.out.print("Enter Account Number: ");
                        int balAccNum = scanner.nextInt();
                        BankAccount balAccount = bank.findAccount(balAccNum);
                        if (balAccount != null) {
                            balAccount.checkBalance();
                        } else {
                            System.out.println("Account not found.");
                        }
                        break;

                    case 5:
                        System.out.print("Enter From Account Number: ");
                        int fromAcc = scanner.nextInt();
                        System.out.print("Enter To Account Number: ");
                        int toAcc = scanner.nextInt();
                        System.out.print("Enter Transfer Amount: ");
                        double transferAmount = scanner.nextDouble();
                        bank.transferMoney(fromAcc, toAcc, transferAmount);
                        break;

                    case 6:
                        System.out.println("Exiting... Thank you for using Online Banking System.");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                scanner.nextLine(); // clear buffer
            }
        }
    }
}
