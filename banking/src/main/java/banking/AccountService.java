package banking;

import java.util.Arrays;
import java.util.Random;

public class AccountService {
    private final Random rd =  new Random();
    private final AccountDB accountDB = new AccountDB();
    private Account account = null;

    public AccountService(String url) {
        connectDB(url);
    }

    private void connectDB(String url) {
        accountDB.getConnection(url);
    }

    public void create() {
        String cardNumber;
        String pin = generatePin();

        do {
            cardNumber = generateCardNumber();
        } while (accountDB.checkNumber(cardNumber));

        if (accountDB.createNewAccount(cardNumber, pin)) {
            System.out.printf("\nYour card has been created" +
                    "\nYour card number:\n%s" +
                    "\nYour card PIN:\n%s\n\n",
                    cardNumber, pin);
        } else System.out.println("Cannot create account!");
    }

    public boolean login(String cardNumber, String pin) {
        account = accountDB.getAccount(cardNumber, pin);
        return account != null;
    }

    public void logout() {
        if (account != null) {
            account = null;
            System.out.println("You have successfully logged out!\n");
        }
    }

    public int getBalance() {
        return account.getBalance();
    }

    public void addBalance(int income) {
        accountDB.addBalance(account.getNumber(), income);
        login(account.getNumber(), account.getPin());
    }

    public boolean checkLuhnAlgo(String cardNumber) {
        if (cardNumber.length() != 16) return false;

        String checkSum = String.valueOf(cardNumber.charAt(cardNumber.length() - 1));
        return checkSum.equals(
                getCheckSum(
                        splitStrToArray(
                                cardNumber.substring(0, cardNumber.length() - 1)
                        )
                )
        );
    }

    public boolean checkExists(String cardNumber) {
        return accountDB.checkNumber(cardNumber);
    }

    public boolean checkBalance(int money) {
        return money <= account.getBalance();
    }

    public void transfer(String cardNumber, int money) {
        if (accountDB.transfer(account.getNumber(), cardNumber, money)) {
            login(account.getNumber(), account.getPin());
        } else {
            System.out.println("Cannot transfer");
        }
    }

    public void deleteAccount() {
        accountDB.delete(account.getId());
    }

    private String generatePin() {
        int lower = 1000;
        int upper = 9999;
        return String.valueOf(rd.nextInt(upper - lower + 1) + lower);
    }

    private String generateCardNumber() {
        int upper = 999999999;
        int lower = 100000000;
        String bankIdentify = "400000";
        int accountIdentify = rd.nextInt(upper - lower + 1) + lower;
        String checkSum = getCheckSum(splitStrToArray(bankIdentify + accountIdentify));
        return bankIdentify + accountIdentify + checkSum;
    }

    private String getCheckSum(int[] numbers) {
        int[] tmpArr = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            if ((i+1) % 2 == 1) {
                int tmpVal = numbers[i] * 2;
                tmpArr[i] = tmpVal > 9 ? tmpVal - 9 : tmpVal;
            } else {
                tmpArr[i] = numbers[i];
            }
        }
        int sum = Arrays.stream(tmpArr).sum();
        return sum % 10 == 0 ? "0" : String.valueOf(10 - (sum % 10));
    }

    private int[] splitStrToArray(String numStr) {
        int[] newGuess = new int[numStr.length()];
        for (int i = 0; i < numStr.length(); i++)
        {
            newGuess[i] = numStr.charAt(i) - '0';
        }
        return newGuess;
    }
}
