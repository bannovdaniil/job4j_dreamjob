package ru.job4j.dreamjob.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Модель вакансий.
 */
public class Vacancy {
    private int id;
    private String title;
    private final String description;
    private final LocalDateTime creationDate;

    public Vacancy(int id, String title, String description, LocalDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vacancy vacancy = (Vacancy) o;
        return id == vacancy.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}