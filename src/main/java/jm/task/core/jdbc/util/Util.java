package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {

    /*private static final Driver driver;

    static {
        try {
            driver = new com.mysql.cj.jdbc.Driver();
            System.out.println("драйвер создан");
            DriverManager.registerDriver(driver);
            System.out.println("драйвер зарегистрирован");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    private static Driver driver;

    private static Connection connection;
    private static final String URL ="jdbc:mysql://localhost:3306/kata_preproject_users";
    private static final String USERNAME ="root";
    private static final String PASSWORD ="root";

    // реализуйте настройку соеденения с БД
    public static Connection getConnection() throws SQLException {

        if (driver == null) {
            driver = new com.mysql.cj.jdbc.Driver();
            System.out.println("драйвер создан");
            DriverManager.registerDriver(driver);
            System.out.println("драйвер зарегистрирован");
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }

        return connection;
    }


}
