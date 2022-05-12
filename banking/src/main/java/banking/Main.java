package banking;

import java.util.Scanner;

public class Main {
    static boolean login = false;
    static Scanner scanner =  new Scanner(System.in);
    static AccountService accountService = null;

    public static void main(String[] args) {
        System.out.println("hello world");
        int choice = Integer.MAX_VALUE;
        accountService = new AccountService("jdbc:sqlite:" + args[1]);
        String cardNumber = "2000007269641768";
        accountService.checkLuhnAlgo(cardNumber);
        while (choice > 0) {
            displayMenu();
            choice = Integer.parseInt(scanner.nextLine());
            doChoice(choice);

            while (login && choice > 0) {
                displayMenu();
                choice = Integer.parseInt(scanner.nextLine());
                doChoice(choice);
            }
        }
    }

    public static void displayMenu() {
        if (login) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Logout");
            System.out.println("0. Exit");
        } else {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
        }
    }

    public static void doChoice(int choice) {
        if (login) {
            switch (choice) {
                case 1: {
                    System.out.printf("\nBalance: %d\n\n", accountService.getBalance());
                    break;
                }
                case 2: {
                    System.out.println("\nEnter income:");
                    int income = Integer.parseInt(scanner.nextLine());
                    accountService.addBalance(income);
                    System.out.println("Income was added!\n");
                    break;
                }
                case 3: {
                    System.out.println("\nTransfer\nEnter card number:");
                    String cardNumber = scanner.nextLine();
                    if (!accountService.checkLuhnAlgo(cardNumber)) {
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                        break;
                    }
                    if (!accountService.checkExists(cardNumber)) {
                        System.out.println("Such a card does not exist.");
                        break;
                    }

                    System.out.println("Enter how much money you want to transfer:");
                    int money = Integer.parseInt(scanner.nextLine());
                    if (!accountService.checkBalance(money)) {
                        System.out.println("Not enough money!");
                        break;
                    }

                    accountService.transfer(cardNumber, money);
                    System.out.println("Success!\n");
                    break;
                }
                case 4: {
                    accountService.deleteAccount();
                    login = false;
                    System.out.println("\nThe account has been closed!\n");
                    break;
                }
                case 5: {
                    accountService.logout();
                    login = false;
                    break;
                }
                case 0: {
                    System.out.println("Bye!");
                }
            }
        } else {
            switch (choice) {
                case 1: {
                    accountService.create();
                    break;
                }
                case 2: {
                    System.out.println("\nEnter your card number:");
                    String cardNumber = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = scanner.nextLine();
                    login = accountService.login(cardNumber, pin);
                    System.out.println(login ? "You have successfully logged in!\n" : "Wrong card number or PIN!\n");
                    break;
                }
                case 3: {
                    System.out.println("Bye!");
                    break;
                }
            }
        }
    }
}