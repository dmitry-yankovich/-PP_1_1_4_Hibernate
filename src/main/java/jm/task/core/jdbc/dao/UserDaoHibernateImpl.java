package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.exception.UtilException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    public UserDaoHibernateImpl() {

    }

    private final SessionFactory sessionFactory = Util.getSessionFactory();

    private static final String CREATE_USERS_TABLE_SQL ="CREATE TABLE IF NOT EXISTS users (\n" +
            "  `id` BIGINT(8) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(100) NULL,\n" +
            "  `lastName` VARCHAR(150) NULL,\n" +
            "  `age` INT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)";
    private static final String DROP_USERS_TABLE_SQL = "DROP TABLE IF EXISTS users";

    public void closeConnection() {
        sessionFactory.close();
    }

    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {

            transaction = session.beginTransaction();

            Query query = session.createSQLQuery(CREATE_USERS_TABLE_SQL).addEntity(User.class);
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {

            transaction = session.beginTransaction();

            Query query = session.createSQLQuery(DROP_USERS_TABLE_SQL).addEntity(User.class);
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {
            /*transaction = session.getTransaction();
            transaction.begin();*/
            transaction = session.beginTransaction();
            User user = new User(name, lastName, age);
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void saveUserList(List<User> userList) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {
            transaction = session.beginTransaction();

            userList.stream().forEachOrdered(x -> saveUser(x.getName(), x.getLastName(), x.getAge()));

            userList.stream().map((x) -> "User с именем – " + x.getName() + " добавлен в базу данных").
                    forEachOrdered(System.out::println);

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {
            transaction = session.beginTransaction();
            User foundUser = (User) session.load(User.class, id);
            session.delete(foundUser);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List <User> userList = new ArrayList();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {
            transaction = session.beginTransaction();
            userList = session.createQuery("FROM User").list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            throw new DaoException(e);
        }

        return userList;
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession();) {
            transaction = session.beginTransaction();
            Query query = session.createQuery("DELETE FROM User");
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void close() throws Exception {
        sessionFactory.close();
    }
}
