package ru.job4j.dreamjob.repository.impl;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.UserRepository;

import java.util.Optional;

@Repository
public class Sql2oUserRepositoryImpl implements UserRepository {
    private final Sql2o sql2o;

    public Sql2oUserRepositoryImpl(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        int generatedId;
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
        } catch (Exception e) {
            return Optional.empty();
        }
        if (generatedId != 0 && user.getId() == generatedId) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            String sql = """
                       SELECT * FROM users 
                        WHERE email = :email AND password = :password;
                    """;
            var query = connection.createQuery(sql);
            query.addParameter("email", email);
            query.addParameter("password", password);
            User user = query.setColumnMappings(Vacancy.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
