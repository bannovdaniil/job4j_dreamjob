package ru.job4j.dreamjob.service.impl;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.impl.MemoryCandidateRepositoryImpl;
import ru.job4j.dreamjob.service.CandidateService;

import java.util.Collection;
import java.util.Optional;

/**
 * Бизнес логика для кандидатов.
 */
public class CandidateServiceImpl implements CandidateService {

    private static final CandidateService INSTANCE = new CandidateServiceImpl();

    private final CandidateRepository candidateRepository = MemoryCandidateRepositoryImpl.getInstance();

    private CandidateServiceImpl() {
    }

    public static CandidateService getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return candidateRepository.deleteById(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidateRepository.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}