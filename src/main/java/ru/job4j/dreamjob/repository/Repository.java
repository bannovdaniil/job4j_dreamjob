package ru.job4j.dreamjob.repository;

import java.util.Collection;
import java.util.Optional;

public interface Repository<E, T> {
    E save(E value);

    boolean deleteById(T id);

    boolean update(E value);

    Optional<E> findById(T id);

    Collection<E> findAll();
}
