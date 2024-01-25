package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FileControllerTest {
    @Mock
    private FileService fileService;
    @InjectMocks
    private FileController fileController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @DisplayName("Get File id 12 - ok")
    @Test
    void getFile() throws Exception {
        int expectedId = 12;
        byte[] expectedBytes = "Test content".getBytes(StandardCharsets.UTF_8);
        FileDto fileDto = new FileDto("file.txt", expectedBytes);
        Mockito.doReturn(Optional.of(fileDto)).when(fileService).getFileById(expectedId);

        mvc.perform(get("/files/" + expectedId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(expectedBytes));

        Mockito.verify(fileService, Mockito.times(1)).getFileById(expectedId);
    }

    @DisplayName("Get File - NotFound")
    @Test
    void whenGetFileThenNotFound() throws Exception {
        Mockito.doReturn(Optional.empty()).when(fileService).getFileById(Mockito.anyInt());

        mvc.perform(get("/files/12"))
                .andExpect(status().isNotFound());

        Mockito.verify(fileService, Mockito.times(1)).getFileById(Mockito.anyInt());
    }
}