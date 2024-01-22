package ru.job4j.dreamjob.repository.impl;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.repository.CityRepository;

import java.util.Collection;

@Repository
public class Sql2oCityRepositoryImpl implements CityRepository {
    private final Sql2o sql2o;

    public Sql2oCityRepositoryImpl(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Collection<City> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM cities");
            return query.executeAndFetch(City.class);
        }
    }
}