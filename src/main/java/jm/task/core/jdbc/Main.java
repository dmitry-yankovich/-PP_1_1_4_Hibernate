package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь

        try (UserService userService = new UserServiceImpl();) {
            userService.createUsersTable();

            List<User> listUser = new ArrayList<>();
            listUser.add(new User("Tom", "Far", (byte) 19));
            listUser.add(new User("Bill", "Geitz", (byte) 20));
            listUser.add(new User("Mike", "Tyson", (byte) 23));
            listUser.add(new User("Nick", "Jagger", (byte) 35));

            userService.saveUserList(listUser);

            userService.getAllUsers().stream().forEachOrdered(System.out::println);

            userService.cleanUsersTable();

            userService.dropUsersTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
