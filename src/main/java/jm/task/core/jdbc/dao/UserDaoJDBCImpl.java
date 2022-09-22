package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.exception.UtilException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    private final Connection connection = Util.getConnection();
    private static final String CREATE_USERS_TABLE_SQL ="CREATE TABLE IF NOT EXISTS users (\n" +
            "  `id` BIGINT(8) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(100) NULL,\n" +
            "  `lastName` VARCHAR(150) NULL,\n" +
            "  `age` INT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)";
    private static final String DROP_USERS_TABLE_SQL = "DROP TABLE IF EXISTS users";
    private static final String INSERT_RECORD_SQL = "insert into users (name, lastName, age) values (?, ?, ?)";
    private static final String CHECK_USER_BY_ID_SQL = "select 1 from users where id = ? limit 1";
    private static final String DELETE_USER_BY_ID_SQL = "delete from users where id = ?";
    private static final String GET_ALL_USERS_SQL = "SELECT * from users";
    private static final String CLEAN_USERS_TABLE_SQL = "truncate table users;";

    public UserDaoJDBCImpl() {

    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new UtilException(e);
        }
    }

    public void createUsersTable() {

        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_USERS_TABLE_SQL);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void dropUsersTable() {

        try (Statement statement = connection.createStatement();) {
            statement.execute(DROP_USERS_TABLE_SQL);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {

        try (PreparedStatement statement = connection.prepareStatement(INSERT_RECORD_SQL)) {
            statement.setString(1, name);
            statement.setString(2, lastName);
            statement.setByte(3, age);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void saveUserList(List<User> userList) {

        try {
            connection.setAutoCommit(false);

            userList.stream().forEachOrdered(x -> saveUser(x.getName(), x.getLastName(), x.getAge()));

            userList.stream().map((x) -> "User с именем – " + x.getName() + " добавлен в базу данных").
                    forEachOrdered(System.out::println);

            connection.commit();

        } catch (DaoException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new UtilException(ex);
            }
            throw new DaoException(e);
        } catch (SQLException | UtilException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new UtilException(ex);
            }
            throw new UtilException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new UtilException(e);
            }
        }
    }

    public void removeUserById(long id) {

            try (PreparedStatement statementCheck = connection.prepareStatement(CHECK_USER_BY_ID_SQL);
                 PreparedStatement statementDelete = connection.prepareStatement(DELETE_USER_BY_ID_SQL)) {

                connection.setAutoCommit(false);

                statementCheck.setLong(1, id);
                ResultSet rs = statementCheck.executeQuery();
                if (!rs.next()) {
                    connection.rollback();
                    return;
                }

                statementDelete.setLong(1, id);
                statementDelete.executeUpdate();

            } catch (SQLException e) {
                try {
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                    }
                } catch (SQLException ex) {
                    throw new UtilException(ex);
                }
                throw new DaoException(e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new UtilException(e);
                }
            }
    }


    public List<User> getAllUsers() {

        List<User> listUsers = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            User user;
            try (ResultSet rs = statement.executeQuery(GET_ALL_USERS_SQL)) {
                while (rs.next()) {
                    user = new User(rs.getString("name"), rs.getString("lastName"), rs.getByte("age"));
                    user.setId(rs.getLong("id"));
                    listUsers.add(user);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return listUsers;
    }

    public void cleanUsersTable() {

            try (Statement statement = connection.createStatement()) {
                statement.execute(CLEAN_USERS_TABLE_SQL);
            } catch (SQLException e) {
                throw new DaoException(e);
            }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
