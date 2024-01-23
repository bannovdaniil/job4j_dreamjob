package ru.job4j.dreamjob.repository.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oVacancyRepositoryImplTest {

    private static Sql2oVacancyRepositoryImpl sql2oVacancyRepository;

    private static Sql2oFileRepositoryImpl sql2oFileRepository;

    private static File file;

    /**
     * Нужно сохранить хотя бы один файл, т.к. Vacancy от него зависит.
     */
    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryImpl.class.getClassLoader().getResourceAsStream("db/liquibase_test.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("url");
        var username = properties.getProperty("username");
        var password = properties.getProperty("password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oVacancyRepository = new Sql2oVacancyRepositoryImpl(sql2o);
        sql2oFileRepository = new Sql2oFileRepositoryImpl(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    void clearVacancies() {
        var vacancies = sql2oVacancyRepository.findAll();
        for (var vacancy : vacancies) {
            sql2oVacancyRepository.deleteById(vacancy.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var savedVacancy = sql2oVacancyRepository.findById(vacancy.getId()).orElseThrow();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(vacancy);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy1 = sql2oVacancyRepository.save(new Vacancy(0, "title1", "description1", creationDate, true, 1, file.getId()));
        var vacancy2 = sql2oVacancyRepository.save(new Vacancy(0, "title2", "description2", creationDate, false, 1, file.getId()));
        var vacancy3 = sql2oVacancyRepository.save(new Vacancy(0, "title3", "description3", creationDate, true, 1, file.getId()));
        var result = sql2oVacancyRepository.findAll();
        assertThat(result).isEqualTo(List.of(vacancy1, vacancy2, vacancy3));
    }

    @Test
    void whenDontSaveThenNothingFound() {
        Collection<Vacancy> all = sql2oVacancyRepository.findAll();
        Optional<Vacancy> vacancy = sql2oVacancyRepository.findById(0);
        assertThat(all).isEqualTo(emptyList());
        assertThat(vacancy).isEmpty();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var isDeleted = sql2oVacancyRepository.deleteById(vacancy.getId());
        var savedVacancy = sql2oVacancyRepository.findById(vacancy.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedVacancy).isEmpty();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oVacancyRepository.deleteById(0)).isFalse();
    }

    @Test
    void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        var updatedVacancy = new Vacancy(
                vacancy.getId(), "new title", "new description", creationDate.plusDays(1),
                !vacancy.getVisible(), 1, file.getId()
        );
        var isUpdated = sql2oVacancyRepository.update(updatedVacancy);
        var savedVacancy = sql2oVacancyRepository.findById(updatedVacancy.getId()).orElseThrow();

        assertThat(vacancy.getCreationDate()).isEqualTo(savedVacancy.getCreationDate());
        assertThat(savedVacancy.getCreationDate()).isNotEqualTo(updatedVacancy.getCreationDate());

        assertThat(isUpdated).isTrue();
        assertThat(savedVacancy)
                .usingRecursiveComparison()
                .ignoringFields("creationDate")
                .isEqualTo(updatedVacancy);
    }

    @Test
    void whenUpdateUnExistingVacancyThenGetFalse() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var vacancy = new Vacancy(0, "title", "description", creationDate, true, 1, file.getId());
        var isUpdated = sql2oVacancyRepository.update(vacancy);
        assertThat(isUpdated).isFalse();
    }
}