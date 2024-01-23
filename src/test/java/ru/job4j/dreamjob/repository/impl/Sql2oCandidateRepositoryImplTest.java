package ru.job4j.dreamjob.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class Sql2oCandidateRepositoryImplTest {
    private static Sql2oCandidateRepositoryImpl sql2oCandidateRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryImpl.class.getClassLoader().getResourceAsStream("db/liquibase_test.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("url");
        var username = properties.getProperty("username");
        var password = properties.getProperty("password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oCandidateRepository = new Sql2oCandidateRepositoryImpl(sql2o);
    }

    @AfterEach
    public void clearCandidates() {
        var candidates = sql2oCandidateRepository.findAll();
        for (var candidate : candidates) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        LocalDateTime creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(0, "name", "description", creationDate));
        Candidate savedCandidate = sql2oCandidateRepository.findById(candidate.getId()).orElseThrow();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = sql2oCandidateRepository.save(new Candidate(0, "name1", "description1", creationDate));
        var candidate2 = sql2oCandidateRepository.save(new Candidate(0, "name2", "description2", creationDate));
        var candidate3 = sql2oCandidateRepository.save(new Candidate(0, "name3", "description3", creationDate));
        var result = sql2oCandidateRepository.findAll();
        assertThat(result).isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    void whenDontSaveThenNothingFound() {
        Collection<Candidate> all = sql2oCandidateRepository.findAll();
        Optional<Candidate> candidate = sql2oCandidateRepository.findById(0);
        assertThat(all).isEqualTo(emptyList());
        assertThat(candidate).isEmpty();
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "title", "description", creationDate));
        var isDeleted = sql2oCandidateRepository.deleteById(candidate.getId());
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEmpty();
    }

    @Test
    void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "name", "description", creationDate));
        var updatedCandidate = new Candidate(candidate.getId(), "new Name", "new description", creationDate.plusDays(1));
        var isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        var savedCandidate = sql2oCandidateRepository.findById(updatedCandidate.getId()).orElseThrow();

        assertThat(candidate.getCreationDate()).isEqualTo(savedCandidate.getCreationDate());
        assertThat(savedCandidate.getCreationDate()).isNotEqualTo(updatedCandidate.getCreationDate());

        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate)
                .usingRecursiveComparison()
                .ignoringFields("creationDate")
                .isEqualTo(updatedCandidate);
    }

    @Test
    void whenUpdateUnExistingVacancyThenGetFalse() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(0, "title", "description", creationDate);
        var isUpdated = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdated).isFalse();
    }
}