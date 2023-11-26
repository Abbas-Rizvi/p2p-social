package network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PeersDatabase {

    private static final String URL = "jdbc:sqlite:data/known_peers.db";

    // Constructor
    public PeersDatabase() {
        try {
            // Create the table if it doesn't exist
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {

        try {

            // Register SQLite driver
            Class.forName("org.sqlite.JDBC");

            Connection connection = null;

            try {
                connection = DriverManager.getConnection(URL);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        } catch (ClassNotFoundException e) {

            // print error
            System.out.println(e.getMessage());

        }

        return DriverManager.getConnection(URL);
    }

    private void createTable() throws SQLException {
        try (Connection connection = connect();
                Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS known_peers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "public_key TEXT NOT NULL," +
                    "ip_address TEXT NOT NULL)";
            statement.executeUpdate(createTableQuery);
        }
    }

    public int insertRecord(String name, String publicKey, String ipAddress) {

        if (isNameExists(name)) {
            return 1;
        }

        try (Connection connection = connect();
                Statement statement = connection.createStatement()) {
            String insertDataQuery = "INSERT INTO known_peers (name, public_key, ip_address) VALUES " +
                    "('" + name + "', '" + publicKey + "', '" + ipAddress + "')";
            statement.executeUpdate(insertDataQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isNameExists(String name) {
        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT COUNT(*) AS count FROM known_peers WHERE name = ?")) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String lookupPublicKeyByName(String name) {
        try (Connection connection = connect();
                Statement statement = connection.createStatement()) {
            String query = "SELECT public_key FROM known_peers WHERE name = '" + name + "'";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    return resultSet.getString("public_key");
                } else {
                    return null; // Name not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String lookupIPAddressByName(String name) {
        try (Connection connection = connect();
                Statement statement = connection.createStatement()) {
            String query = "SELECT ip_address FROM known_peers WHERE name = '" + name + "'";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    return resultSet.getString("ip_address");
                } else {
                    return null; // Name not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String lookupNameByPublicKey(String publicKey) {
        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT name FROM known_peers WHERE public_key = ?")) {

            preparedStatement.setString(1, publicKey);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllMatchingRows(String name) {
        List<String> matchingRows = new ArrayList<>();

        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM known_peers WHERE name = ?")) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String row = String.format("ID: %d, Name: %s, Public Key: %s, IP Address: %s",
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("public_key"),
                            resultSet.getString("ip_address"));
                    matchingRows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchingRows;
    }

    public void deleteRowsByName(String name) {
        try (Connection connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM known_peers WHERE name = ?")) {

            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Node> readAllNodes() {
        List<Node> allNodes = new ArrayList<>();

        try (Connection connection = connect();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM known_peers")) {

            while (resultSet.next()) {

                Node node = new Node(
                        resultSet.getString("name"),
                        resultSet.getString("public_key"),
                        resultSet.getString("ip_address"));

                allNodes.add(node);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allNodes;
    }
}
