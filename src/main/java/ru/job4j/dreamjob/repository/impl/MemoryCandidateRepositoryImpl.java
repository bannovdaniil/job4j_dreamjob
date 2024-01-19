package ru.job4j.dreamjob.repository.impl;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In memory репозиторий кандидатов.
 */
@Repository
@ThreadSafe
public class MemoryCandidateRepositoryImpl implements CandidateRepository {
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    private MemoryCandidateRepositoryImpl() {
        save(new Candidate(0, "Иван Иванов", "Intern Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Федя Пупкин", "Junior Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Саня Полукедов", "Junior+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Семен Семёныч", "Middle Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Митя Аристархович", "Middle+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Фон Германов", "Senior Java Developer", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(Integer id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return
                candidates.computeIfPresent(candidate.getId(),
                        (id, oldCandidate) -> new Candidate(
                                oldCandidate.getId(),
                                candidate.getName(),
                                candidate.getDescription(),
                                oldCandidate.getCreationDate())
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
