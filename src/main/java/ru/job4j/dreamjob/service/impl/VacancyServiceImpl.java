package ru.job4j.dreamjob.service.impl;

import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;
import ru.job4j.dreamjob.repository.impl.MemoryVacancyRepositoryImpl;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.Collection;
import java.util.Optional;

/**
 * Бизнес логика для Вакансий.
 */
public class VacancyServiceImpl implements VacancyService {

    private static final VacancyService INSTANCE = new VacancyServiceImpl();

    private final VacancyRepository vacancyRepository = MemoryVacancyRepositoryImpl.getInstance();

    private VacancyServiceImpl() {
    }

    public static VacancyService getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return vacancyRepository.save(vacancy);
    }

    @Override
    public boolean deleteById(int id) {
        return vacancyRepository.deleteById(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancyRepository.update(vacancy);
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}