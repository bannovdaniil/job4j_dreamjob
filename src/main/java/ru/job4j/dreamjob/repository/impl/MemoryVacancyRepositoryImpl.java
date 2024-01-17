package ru.job4j.dreamjob.repository.impl;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In memory репозиторий вакансий.
 */
@Repository
public class MemoryVacancyRepositoryImpl implements VacancyRepository {
    private static final MemoryVacancyRepositoryImpl INSTANCE = new MemoryVacancyRepositoryImpl();
    private int nextId = 1;
    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepositoryImpl() {
        save(new Vacancy(0, "Intern Java Developer", "Java core", LocalDateTime.now()));
        save(new Vacancy(0, "Junior Java Developer", "Java core, Stream API", LocalDateTime.now()));
        save(new Vacancy(0, "Junior+ Java Developer", "Java core, Collections, Stream, OOP", LocalDateTime.now()));
        save(new Vacancy(0, "Middle Java Developer", "Java core, SpringBoot", LocalDateTime.now()));
        save(new Vacancy(0, "Middle+ Java Developer", "Java core, SpringBoot, Docker", LocalDateTime.now()));
        save(new Vacancy(0, "Senior Java Developer", "Java core, Docker, Jenkins, K8s", LocalDateTime.now()));
    }

    public static MemoryVacancyRepositoryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(Integer id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return
                vacancies.computeIfPresent(vacancy.getId(),
                        (id, oldVacancy) -> new Vacancy(
                                oldVacancy.getId(),
                                vacancy.getTitle(),
                                vacancy.getDescription(),
                                oldVacancy.getCreationDate())
                ) != null;
    }

    @Override
    public Optional<Vacancy> findById(Integer id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}