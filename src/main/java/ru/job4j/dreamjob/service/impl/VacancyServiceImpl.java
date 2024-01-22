package ru.job4j.dreamjob.service.impl;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.Collection;
import java.util.Optional;

/**
 * Бизнес логика для Вакансий.
 */
@Service
@ThreadSafe
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final FileService fileService;

    public VacancyServiceImpl(VacancyRepository sql2oVacancyRepositoryImpl, FileService fileService) {
        this.vacancyRepository = sql2oVacancyRepositoryImpl;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto fileDto) {
        saveNewFile(vacancy, fileDto);
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto fileDto) {
        File file = fileService.save(fileDto);
        vacancy.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var vacancy = findById(id);
        if (vacancy.isPresent()) {
            vacancyRepository.deleteById(id);
            fileService.deleteById(vacancy.get().getFileId());
        }
        return vacancyRepository.deleteById(id);
    }

    /**
     * Если передан новый не пустой файл, то старый удаляем, а новый сохраняем
     */
    @Override
    public boolean update(Vacancy vacancy, FileDto fileDto) {
        var isNewFileEmpty = fileDto.getContent().length == 0;
        if (isNewFileEmpty) {
            return vacancyRepository.update(vacancy);
        }
        var oldFileId = vacancy.getFileId();
        saveNewFile(vacancy, fileDto);
        var isUpdated = vacancyRepository.update(vacancy);
        fileService.deleteById(oldFileId);
        return isUpdated;
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