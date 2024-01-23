package ru.job4j.dreamjob.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryImplTest {
    private static Sql2oUserRepositoryImpl sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryImpl.class.getClassLoader().getResourceAsStream("db/liquibase_test.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("url");
        var username = properties.getProperty("username");
        var password = properties.getProperty("password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepositoryImpl(sql2o);
        clearUsersTable();
    }

    private static void clearUsersTable() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("TRUNCATE TABLE users;");
            query.executeUpdate();
        }
    }

    @AfterEach
    public void clearUsers() {
        clearUsersTable();
    }

    @DisplayName("Save and Find User by email and password Then Same")
    @Test
    void whenSaveThenGetSame() {
        User user = sql2oUserRepository.save(new User(0, "name@test.ru", "Egor", "password")).orElseThrow();
        Optional<User> savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get()).usingRecursiveComparison().isEqualTo(user);
    }

    @DisplayName("Save double Email then get Empty")
    @Test
    void whenSaveDoubleEmailThenGetEmpty() {
        Optional<User> user1 = sql2oUserRepository.save(new User(0, "name@test.ru", "Egor", "password"));
        Optional<User> user2 = sql2oUserRepository.save(new User(0, "name@test.ru", "Semen", "test"));
        assertThat(user1).isPresent();
        assertThat(user2).isEmpty();
    }

    @DisplayName("Find User by email and Wrong password Then Emptu")
    @Test
    void findByEmailAndWrongPasswordThenError() {
        User user = sql2oUserRepository.save(new User(0, "name@test.ru", "Egor", "password")).orElseThrow();
        Optional<User> findUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), "bad password");
        assertThat(findUser).isEmpty();
    }

    @DisplayName("Find User by Wrong email and password Then Emptu")
    @Test
    void findByWrongEmailAndPasswordThenError() {
        User user = sql2oUserRepository.save(new User(0, "name@test.ru", "Egor", "password")).orElseThrow();
        Optional<User> findUser = sql2oUserRepository.findByEmailAndPassword("wrong@user.ru", user.getPassword());
        assertThat(findUser).isEmpty();
    }

}