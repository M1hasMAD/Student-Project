package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.exception.UserNotFoundException;
import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.User;
import com.mikhail.shop.cars.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceUsersImplJPA implements ServiceUsers {

    private final UserRepository userRepository;

    public ServiceUsersImplJPA(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmailYPass(String email, String pass) {
        return userRepository.findByEmailIgnoreCaseAndPass(email.trim(), pass);
    }

    @Override
    public void registerUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveChangesOfUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}