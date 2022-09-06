package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {

        String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                "  `id` BIGINT(8) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(100) NULL,\n" +
                "  `lastName` VARCHAR(150) NULL,\n" +
                "  `age` INT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);";

        try {
            Connection connection = Util.getConnection();
            try(Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS users ;";

        try {
            try (Connection connection = Util.getConnection()) {
                Statement statement = connection.createStatement();
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        String sql = "insert into users (name, lastName, age) values (?, ?, ?)";

        try {
            Connection connection = Util.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, lastName);
                statement.setByte(3, age);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUserById(long id) {
        String sqlCheck = "select 1 from users where id = ? limit 1";
        String sqlDelete = "delete from users where id = ?";

        try {
            Connection connection = Util.getConnection();

            try (PreparedStatement statementCheck = connection.prepareStatement(sqlCheck)) {
                statementCheck.setLong(1, id);
                ResultSet rs = statementCheck.executeQuery();
                if (!rs.next()) {
                    return;
                }
            }

            try (PreparedStatement statementDelete = connection.prepareStatement(sqlDelete)) {
                statementDelete.setLong(1, id);
                statementDelete.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {

        List <User> listUsers = new ArrayList<>();

        String sql = "SELECT * from users;";

        try {
            Connection connection = Util.getConnection();
            try (Statement statement = connection.createStatement()) {
                User user;
                try (ResultSet rs = statement.executeQuery(sql)) {
                    while (rs.next()) {
                        user = new User(rs.getString("name"), rs.getString("lastName"), rs.getByte("age"));
                        user.setId(rs.getLong("id"));
                        listUsers.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listUsers;
    }

    public void cleanUsersTable() {
        //String sql = "delete from users;";
        String sql = "truncate table users;";

        try {
            Connection connection = Util.getConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
