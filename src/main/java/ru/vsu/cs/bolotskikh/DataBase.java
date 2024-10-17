package ru.vsu.cs.bolotskikh;

import java.sql.*;
import java.util.*;

public class DataBase {
    public static Connection connection;


    public static void connect(String url) throws Exception{
        if(connection == null){
            connection = DriverManager.getConnection(url);
        }else {
            connection.close();
            connection = DriverManager.getConnection(url);
        }
    }


    public static void createDbLogins() throws ClassNotFoundException, SQLException
    {
        try {
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE if not exists 'logins' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' TEXT, 'description' TEXT, 'pass_id' TEXT);");


            statement.close();
            connection.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDbPass() throws ClassNotFoundException, SQLException
    {
        try {
            connect("jdbc:sqlite:db/passwords.s3db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE if not exists 'passwords' ('id' TEXT, 'password' TEXT, 'key_id' TEXT);");


            statement.close();
            connection.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDbKeys() throws ClassNotFoundException, SQLException
    {
        try {
            connect("jdbc:sqlite:db/keys.s3db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE if not exists 'keys' ('id' TEXT, 'key' TEXT);");


            statement.close();
            connection.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static ResultSet getQuery(String query){
        try {
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);


            statement.close();
            connection.close();
            return results;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDescription(String login){
        try {
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT description FROM logins WHERE login='" + login + "'");
            return result.getString("description");
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Map<String, String> allLogins(){
        Map<String, String> logins = new HashMap<>();

        try {
            // Явно регистрируем драйвер (необязательно)
            Class.forName("org.sqlite.JDBC"); // Замените на имя класса вашего драйвера
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT login, description FROM logins");
            while (results.next()){
                logins.put(results.getString("login"), results.getString("description"));
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return logins;
    }


    public static List<String> allKey(){
        List<String> keys = new ArrayList<>();

        try {
            // Явно регистрируем драйвер (необязательно)
            Class.forName("org.sqlite.JDBC"); // Замените на имя класса вашего драйвера
            connect("jdbc:sqlite:db/keys.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT id FROM keys");
            while (results.next()){
                keys.add(results.getString("id"));
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keys;
    }




    public static void addLogins(String login, String description){
        try {
            Class.forName("org.sqlite.JDBC");
            connect("jdbc:sqlite:db/logins.s3db");

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT COUNT(login) FROM logins");
            int count = results.getInt(1) + 1;

            try (PreparedStatement statement1 = connection.prepareStatement(
                    "INSERT INTO logins(`id`, `login`, 'description', `pass_id`) " +
                            "VALUES(?, ?, ?, ?)")) {
                statement1.setObject(1, count);
                statement1.setObject(2, login);
                statement1.setObject(3, description);
                statement1.setObject(4, UUID.randomUUID());
                // Выполняем запрос
                statement1.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }


            statement.close();
            connection.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static List<String> allLoginsPass(String login){
        try {
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT pass_id FROM logins WHERE login='" + login + "'");
            String pass_id = results.getString("pass_id");
            statement.close();

            connect("jdbc:sqlite:db/passwords.s3db");
            statement = connection.createStatement();
            results = statement.executeQuery("SELECT password, key_id FROM passwords WHERE id='" + pass_id + "'");
            Map<String, String> passwords = new HashMap<>();
            while (results.next()){
                passwords.put(results.getString("password"), results.getString("key_id"));
            }
            statement.close();

            connect("jdbc:sqlite:db/keys.s3db");
            statement = connection.createStatement();
            for(String pass: passwords.keySet()){
                results = statement.executeQuery("SELECT key FROM keys WHERE id='" + passwords.get(pass) + "'");
                passwords.put(pass, results.getString("key"));
            }
            // Конечный список паролей пользователя
            List<String> allPasswords = new ArrayList<>();
            Encryption encryption = new Encryption();
            for (String pass: passwords.keySet()){
                allPasswords.add(encryption.decoding(pass, passwords.get(pass)));
            }


            statement.close();
            connection.close();
            return allPasswords;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void addNewLogin(String login, String description) {

        try {
            Class.forName("org.sqlite.JDBC");
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT COUNT(*) FROM logins WHERE login='" + login + "'");
            if(results.getInt(1) == 1){
                System.out.println("Логин занят");
                return;
            }


            try (PreparedStatement statement1 = connection.prepareStatement(
                    "INSERT INTO logins(`login`, 'description', `pass_id`) " +
                            "VALUES(?, ?, ?)")) {
                statement1.setObject(1, login);
                statement1.setObject(2, description);
                statement1.setObject(3, UUID.randomUUID());
                // Выполняем запрос
                statement1.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            statement.close();
            connection.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void addNewPass(String login, String pass){
        List<String> encryptionPass;
        try {
            Encryption encryption = new Encryption();
            encryptionPass = encryption.encrypting(pass);

            Class.forName("org.sqlite.JDBC");
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT pass_id FROM logins WHERE login='" + login + "'");
            String pass_id = results.getString("pass_id");
            UUID key_id = UUID.randomUUID();
            statement.close();


            connect("jdbc:sqlite:db/passwords.s3db");
            try (PreparedStatement statement1 = connection.prepareStatement(
                    "INSERT INTO passwords(`id`, `password`, `key_id`) " +
                            "VALUES(?, ?, ?)")) {
                statement1.setObject(1, pass_id);
                statement1.setObject(2, encryptionPass.get(1));
                statement1.setObject(3, key_id);
                // Выполняем запрос
                statement1.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            statement.close();

            connect("jdbc:sqlite:db/keys.s3db");
            try (PreparedStatement statement1 = connection.prepareStatement(
                    "INSERT INTO keys(`id`, `key`) " +
                            "VALUES(?, ?)")) {
                statement1.setObject(1, key_id);
                statement1.setObject(2, encryptionPass.get(0));
                // Выполняем запрос
                statement1.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void delLogin(String login) {
        try {
            Class.forName("org.sqlite.JDBC");
            connect("jdbc:sqlite:db/logins.s3db");
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT pass_id FROM logins WHERE login='" + login + "'");
            String pass_id = results.getString("pass_id");
            statement.execute("DELETE FROM logins WHERE login='" + login + "'");
            statement.close();


            connect("jdbc:sqlite:db/passwords.s3db");
            statement = connection.createStatement();
            results = statement.executeQuery("SELECT key_id FROM passwords WHERE id='" + pass_id + "'");
            String key_id = results.getString("key_id");
            statement.execute("DELETE FROM passwords WHERE id='" + pass_id + "'");
            statement.close();

            connect("jdbc:sqlite:db/keys.s3db");
            statement = connection.createStatement();
            results = statement.executeQuery("SELECT id FROM keys");
            String key = results.getString("id");
            statement.execute("DELETE FROM keys WHERE id='" + key_id + "'");
            statement.close();
            connection.close();

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
