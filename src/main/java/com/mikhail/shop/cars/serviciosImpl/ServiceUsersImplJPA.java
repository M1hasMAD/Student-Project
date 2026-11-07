package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.exception.UserNotFoundException;
import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.User;
import com.mikhail.shop.cars.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceUsersImplJPA implements ServiceUsers {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ServiceUsersImplJPA(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String email, Pageable pageable) {
        if (email == null || email.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByEmailContainingIgnoreCase(email.trim(), pageable);
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
        return userRepository.findByEmailIgnoreCase(email.trim())
                .filter(user -> checkPassword(pass != null ? pass.trim() : "", user.getPass()));
    }

    @Override
    public void registerUser(User user) {
        user.setPass(passwordEncoder.encode(user.getPass()));
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
    public void changeUserRole(long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}