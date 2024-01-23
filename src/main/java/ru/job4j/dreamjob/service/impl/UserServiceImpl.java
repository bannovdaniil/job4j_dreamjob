package ru.job4j.dreamjob.service.impl;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.UserRepository;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

/**
 * Бизнес логика для Пользователей.
 */
@Service
@ThreadSafe
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository sql2oUserRepositoryImpl) {
        this.userRepository = sql2oUserRepositoryImpl;
    }

    @Override
    public Optional<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}