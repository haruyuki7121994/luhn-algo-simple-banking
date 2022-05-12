package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class AccountDB {
    private final SQLiteDataSource dataSource = new SQLiteDataSource();

    public void getConnection(String url) {
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(id INTEGER PRIMARY KEY, number TEXT NOT NULL, pin TEXT NOT NULL, balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account getAccount(String number, String pin) {
        Account result = null;
        String sql = "SELECT * FROM card WHERE number = ? and pin = ?";
        
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);

            ResultSet accounts = preparedStatement.executeQuery();

            if (accounts.next()) {
                result = new Account();
                // Retrieve column values
                result.setId(accounts.getInt("id"));
                result.setNumber(accounts.getString("number"));
                result.setPin(accounts.getString("pin"));
                result.setBalance(accounts.getInt("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean checkNumber(String number) {
        String sql = "SELECT * FROM card where number = ?";
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);

            ResultSet accounts = preparedStatement.executeQuery();

            if (accounts.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createNewAccount(String number, String pin) {
        int i = 0;
        String sql = "INSERT INTO card (number, pin) VALUES (?, ?)";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);

            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i == 1;
    }

    public void addBalance(String cardNumber, int income) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setFloat(1, income);
            preparedStatement.setString(2, cardNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean transfer(String fromNumber, String toNumber, int money) {
        String withdrawSql = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String addIncomeSql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (Connection con = dataSource.getConnection()) {

            // Disable auto-commit mode
            con.setAutoCommit(false);

            try (PreparedStatement withdraw = con.prepareStatement(withdrawSql);
                 PreparedStatement addIncome = con.prepareStatement(addIncomeSql)) {

                // Withdraw from number
                withdraw.setFloat(1, money);
                withdraw.setString(2, fromNumber);
                withdraw.executeUpdate();

                // Add income to number
                addIncome.setFloat(1, money);
                addIncome.setString(2, toNumber);
                addIncome.executeUpdate();

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void delete(int id) {
        String sql = "DELETE FROM card WHERE id = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setFloat(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
