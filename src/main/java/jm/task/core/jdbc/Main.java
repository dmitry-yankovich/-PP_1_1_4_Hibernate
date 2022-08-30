package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        //Util util = new Util();
        try(Connection connection = Util.getConnection()) {
            UserService userService = new UserServiceImpl();
            userService.createUsersTable();

            List<User> listUser = new ArrayList<>();
            listUser.add(new User("Tom", "Far", (byte) 19));
            listUser.add(new User("Bill", "Geitz", (byte) 20));
            listUser.add(new User("Mike", "Tyson", (byte) 23));
            listUser.add(new User("Nick", "Jagger", (byte) 35));

            listUser.stream().peek(x -> userService.saveUser(x.getName(), x.getLastName(), x.getAge())).
                    map((x) -> "User с именем – " + x.getName() + " добавлен в базу данных").
                    forEachOrdered(System.out::println);

            userService.getAllUsers().stream().forEachOrdered(System.out::println);

            userService.cleanUsersTable();

            userService.dropUsersTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
