import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Account class
class Account implements Serializable {
    private String accountNumber;
    private int pin;
    private double balance;

    public Account(String accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean validatePin(int inputPin) {
        return this.pin == inputPin;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Amount deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Amount withdrawn: " + amount);
        } else if (amount > balance) {
            System.out.println("Insufficient balance.");
        } else {
            System.out.println("Invalid withdrawal amount.");
        }
    }
}

// ATM System
public class ATMSimulation {
    private static final String FILE_NAME = "accounts.dat";
    private static Map<String, Account> accounts = new HashMap<>();

    // Load accounts from file
    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (Map<String, Account>) ois.readObject();
        } catch (Exception e) {
            accounts = new HashMap<>(); // If file not found or empty
        }
    }

    // Save accounts to file
    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
        } catch (Exception e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static void atmMenu(Account account, Scanner sc) {
        int choice;
        do {
            System.out.println("\n---- ATM Menu ----");
            System.out.println("1. Balance Inquiry");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: " + account.getBalance());
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = sc.nextDouble();
                    account.deposit(depositAmount);
                    saveAccounts();
                    break;
                case 3:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = sc.nextDouble();
                    account.withdraw(withdrawAmount);
                    saveAccounts();
                    break;
                case 4:
                    System.out.println("Exiting... Thank you for using the ATM.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 4);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadAccounts(); // Load existing accounts

        System.out.println("====== Welcome to Java ATM Simulation ======");
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        if (accounts.containsKey(accNo)) {
            // Existing user
            System.out.print("Enter PIN: ");
            int pin = sc.nextInt();

            Account acc = accounts.get(accNo);
            if (acc.validatePin(pin)) {
                System.out.println("Login Successful!");
                atmMenu(acc, sc);
            } else {
                System.out.println("Invalid PIN. Access Denied.");
            }
        } else {
            // New user → create account
            System.out.println("No account found. Creating a new one...");
            System.out.print("Set a 4-digit PIN: ");
            int pin = sc.nextInt();

            System.out.print("Enter initial balance: ");
            double balance = sc.nextDouble();

            Account newAcc = new Account(accNo, pin, balance);
            accounts.put(accNo, newAcc);
            saveAccounts();

            System.out.println("Account created successfully! You can now log in next time.");
        }
    }
}
