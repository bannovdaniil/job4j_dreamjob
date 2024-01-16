package ru.job4j.dreamjob.repository.impl;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In memory репозиторий кандидатов.
 */
@Repository
public class MemoryCandidateRepositoryImpl implements CandidateRepository {
    private static final MemoryCandidateRepositoryImpl INSTANCE = new MemoryCandidateRepositoryImpl();
    private int nextId = 1;
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepositoryImpl() {
        save(new Candidate(0, "Иван Иванов", "Intern Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Федя Пупкин", "Junior Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Саня Полукедов", "Junior+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Семен Семёныч", "Middle Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Митя Аристархович", "Middle+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Фон Германов", "Senior Java Developer", LocalDateTime.now()));
    }

    public static MemoryCandidateRepositoryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(Integer id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return
                candidates.computeIfPresent(candidate.getId(),
                        (id, oldCandidate) -> new Candidate(
                                oldCandidate.getId(),
                                candidate.getName(),
                                candidate.getDescription(),
                                candidate.getCreationDate())
                ) != null;
    }

    @Override
    public Optional<Candidate> findById(Integer id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
