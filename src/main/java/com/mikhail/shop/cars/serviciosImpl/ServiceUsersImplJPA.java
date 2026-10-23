package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServiceUsersImplJPA implements ServiceUsers {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getUsers() {
        return entityManager
                .createQuery("select u from User u", User.class)
                .getResultList();
    }

    @Override
    public User getUserById(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        List<User> result = entityManager
                .createQuery(
                        "select u from User u where lower(u.email) = lower(:email)",
                        User.class
                )
                .setParameter("email", email.trim())
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User getUserByEmailYPass(String email, String pass) {
        List<User> result = entityManager
                .createQuery(
                        "select u from User u " +
                                "where lower(u.email) = lower(:email) and u.pass = :pass",
                        User.class
                )
                .setParameter("email", email.trim())
                .setParameter("pass", pass)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void registerUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void saveChangesOfUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public void deleteUserById(long id) throws Exception {
        User user = entityManager.find(User.class, id);

        if (user == null) {
            throw new Exception("User ID not found");
        }

        entityManager.remove(user);
    }
}