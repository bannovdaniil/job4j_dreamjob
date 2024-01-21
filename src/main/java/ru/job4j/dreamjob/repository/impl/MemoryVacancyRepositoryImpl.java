package ru.job4j.dreamjob.repository.impl;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In memory репозиторий вакансий.
 */
@Repository
@ThreadSafe
public class MemoryVacancyRepositoryImpl implements VacancyRepository {
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public MemoryVacancyRepositoryImpl() {
        save(new Vacancy(0, "Intern Java Developer", "Java core", LocalDateTime.now(), true, 1));
        save(new Vacancy(0, "Junior Java Developer", "Java core, Stream API", LocalDateTime.now(), true, 2));
        save(new Vacancy(0, "Junior+ Java Developer", "Java core, Collections, Stream, OOP", LocalDateTime.now(), true, 3));
        save(new Vacancy(0, "Middle Java Developer", "Java core, SpringBoot", LocalDateTime.now(), true, 1));
        save(new Vacancy(0, "Middle+ Java Developer", "Java core, SpringBoot, Docker", LocalDateTime.now(), true, 2));
        save(new Vacancy(0, "Senior Java Developer", "Java core, Docker, Jenkins, K8s", LocalDateTime.now(), false, 3));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(Integer id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return
                vacancies.computeIfPresent(vacancy.getId(),
                        (id, oldVacancy) -> new Vacancy(
                                oldVacancy.getId(),
                                vacancy.getTitle(),
                                vacancy.getDescription(),
                                oldVacancy.getCreationDate(),
                                vacancy.getVisible(),
                                vacancy.getCityId()
                        )
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