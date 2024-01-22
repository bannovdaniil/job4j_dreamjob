package ru.job4j.dreamjob.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.exception.FileException;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.FileRepository;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final String storageDirectory;

    public FileServiceImpl(FileRepository sql2oFileRepositoryImpl, @Value("${file.directory}") String storageDirectory) {
        this.fileRepository = sql2oFileRepositoryImpl;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            if (Files.notExists(Path.of(path))) {
                Files.createDirectories(Path.of(path));
            }
        } catch (IOException e) {
            throw new FileException("Не смог создать путь для хранения файлов.");
        }
    }

    @Override
    public File save(FileDto fileDto) {
        var path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return fileRepository.save(new File(fileDto.getName(), path));
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new FileException("Не смог сохранить данные в файл.");
        }
    }

    @Override
    public Optional<FileDto> getFileById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(fileOptional.get().getPath());
        return Optional.of(new FileDto(fileOptional.get().getName(), content));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new FileException("Не смог прочитать данные из файла.");
        }
    }

    @Override
    public void deleteById(int id) {
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            deleteFile(fileOptional.get().getPath());
            fileRepository.deleteById(id);
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new FileException("Не смог удалить файл.");
        }
    }
}
