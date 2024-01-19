package ru.job4j.dreamjob.service.impl;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.Collection;
import java.util.Optional;

/**
 * Бизнес логика для Вакансий.
 */
@Service
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;

    private VacancyServiceImpl(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
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