package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CandidateControllerTest {
    @Mock
    private CandidateService candidateService;
    @InjectMocks
    private CandidateController candidateController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(candidateController).build();
    }

    @DisplayName("Get all resume of candidates")
    @Test
    void getAll() throws Exception {
        Collection<Candidate> candidateList = List.of(
                new Candidate(1, "name1", "desc1", LocalDateTime.now()),
                new Candidate(2, "name2", "desc2", LocalDateTime.now()),
                new Candidate(3, "name3", "desc3", LocalDateTime.now())
        );
        Mockito.doReturn(candidateList).when(candidateService).findAll();

        mvc.perform(get("/candidates"))
                .andExpect(status().isOk())
                .andExpect(view().name("candidates/list"))
                .andExpect(model().attributeExists("candidates"))
                .andExpect(model().attribute("candidates", candidateList));

        Mockito.verify(candidateService, Mockito.times(1)).findAll();
    }

    @DisplayName("Show resume creation page")
    @Test
    void getCreationPage() throws Exception {
        mvc.perform(get("/candidates/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("candidates/create"));
    }

    @DisplayName("Create candidate resume")
    @Test
    void createPost() throws Exception {
        int expectedId = 3;
        Candidate expectedCandidate = new Candidate(expectedId, "name3", "desc3", LocalDateTime.now());

        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        Mockito.doReturn(expectedCandidate).when(candidateService).save(candidateArgumentCaptor.capture());

        var model = new ConcurrentModel();
        String view = candidateController.createPost(expectedCandidate, model);
        var resultCandidate = candidateArgumentCaptor.getValue();

        Mockito.verify(candidateService, Mockito.times(1)).save(Mockito.any());

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(resultCandidate).isEqualTo(expectedCandidate);

        mvc.perform(post("/candidates/create")
                        .content("id=" + expectedCandidate.getId()
                                + "&name=" + expectedCandidate.getName()
                                + "&description=" + expectedCandidate.getDescription()
                        )
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/candidates"));
    }

    @DisplayName("When Create candidate Then Exception")
    @Test
    void whenCreateThenError() throws Exception {
        int expectedId = 3;
        Candidate expectedCandidate = new Candidate(expectedId, "name3", "desc3", LocalDateTime.now());

        var expectedException = new RuntimeException("Failed to create resume.");
        Mockito.when(candidateService.save(Mockito.any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        String view = candidateController.createPost(expectedCandidate, model);
        var actualExceptionMessage = model.getAttribute("message");

        Mockito.verify(candidateService, Mockito.times(1)).save(Mockito.any());

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());

        mvc.perform(post("/candidates/create")
                        .content("id=" + expectedCandidate.getId()
                                + "&name=" + expectedCandidate.getName()
                                + "&description=" + expectedCandidate.getDescription()
                        )
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/404"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", expectedException.getMessage()));
    }

    @DisplayName("Get by ID - errors/404 page")
    @Test
    void findByIdNotFound() throws Exception {
        int expectedId = 12;
        Mockito.doReturn(Optional.empty()).when(candidateService).findById(expectedId);

        mvc.perform(get("/candidates/" + expectedId))
                .andExpect(view().name("errors/404"));

        Mockito.verify(candidateService).findById(expectedId);
    }

    @DisplayName("Delete by ID -  errors/404 page")
    @Test
    void deleteByIdNotFound() throws Exception {
        int expectedId = 12;
        Mockito.doReturn(false).when(candidateService).deleteById(expectedId);

        mvc.perform(get("/candidates/delete/" + expectedId))
                .andExpect(view().name("errors/404"));

        Mockito.verify(candidateService).deleteById(expectedId);
    }

    @DisplayName("Delete by ID - is OK")
    @Test
    void deleteByIdWhenOk() throws Exception {
        int expectedId = 12;
        Mockito.doReturn(true).when(candidateService).deleteById(expectedId);

        mvc.perform(get("/candidates/delete/" + expectedId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/candidates"));

        Mockito.verify(candidateService).deleteById(expectedId);
    }

    @DisplayName("Update by ID -  errors/404 page")
    @Test
    void updateByIdNotFound() throws Exception {
        Candidate expectedCandidate = new Candidate(3, "name3", "desc3", LocalDateTime.now());

        Mockito.doReturn(false).when(candidateService).update(Mockito.any());

        var model = new ConcurrentModel();
        String view = candidateController.update(expectedCandidate, model);

        Mockito.verify(candidateService).update(Mockito.any());
        assertThat(view).isEqualTo("errors/404");
    }

    @DisplayName("Update by ID - OK")
    @Test
    void updateById() throws Exception {
        Candidate expectedCandidate = new Candidate(3, "name3", "desc3", LocalDateTime.now());

        Mockito.doReturn(true).when(candidateService).update(expectedCandidate);

        var model = new ConcurrentModel();
        String view = candidateController.update(expectedCandidate, model);

        Mockito.verify(candidateService).update(expectedCandidate);
        assertThat(view).isEqualTo("redirect:/candidates");
    }
}