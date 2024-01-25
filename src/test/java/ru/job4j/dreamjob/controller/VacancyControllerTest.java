package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class VacancyControllerTest {
    private VacancyService vacancyService;
    private MockMvc mvc;
    private CityService cityService;
    private VacancyController vacancyController;
    private MultipartFile testFile;

    @BeforeEach
    void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});

        mvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
    }

    @DisplayName("getAll")
    @Test
    void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @DisplayName("getAll с городами")
    @Test
    void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @DisplayName("getAll с файлом")
    @Test
    void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.createPost(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @DisplayName("getAll сообщение об ошибке о записи в файл.")
    @Test
    void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.createPost(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @DisplayName("GET Creation Page")
    @Test
    void getCreationPage() throws Exception {
        mvc.perform(get("/vacancies/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cities"))
                .andExpect(view().name("vacancies/create"));
    }

    @DisplayName("POST createPost")
    @Test
    void createPost() throws Exception {
        Vacancy vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);

        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), Mockito.any())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        String view = vacancyController.createPost(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();

        Mockito.verify(vacancyService).save(Mockito.any(), Mockito.any());

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
    }

    @DisplayName("GET vacancies by ID")
    @Test
    void getById() throws Exception {
        Integer expectedId = 1;
        Vacancy expectedVacancy = new Vacancy(expectedId, "test1", "desc1", now(), true, 1, 2);

        Mockito.doReturn(Optional.of(expectedVacancy)).when(vacancyService).findById(Mockito.anyInt());

        mvc.perform(get("/vacancies/" + expectedId))
                .andExpect(status().isOk())
                .andExpect(view().name("vacancies/one"))
                .andExpect(model().attributeExists("vacancy"))
                .andExpect(model().attribute("vacancy", expectedVacancy));

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(vacancyService).findById(argumentCaptor.capture());
        int result = argumentCaptor.getValue();

        assertThat(result).isEqualTo(expectedId);
    }

    @DisplayName("Get by ID - errors/404 page")
    @Test
    void findByIdNotFound() throws Exception {
        Mockito.doReturn(Optional.empty()).when(vacancyService).findById(Mockito.anyInt());

        mvc.perform(get("/vacancies/10"))
                .andExpect(view().name("errors/404"));

        Mockito.verify(vacancyService).findById(Mockito.anyInt());
    }

    @DisplayName("Delete by ID - is OK")
    @Test
    void deleteByIdWhenOk() throws Exception {
        Mockito.doReturn(true).when(vacancyService).deleteById(Mockito.anyInt());

        mvc.perform(get("/vacancies/delete/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vacancies"));

        Mockito.verify(vacancyService).deleteById(Mockito.anyInt());
    }

    @DisplayName("Delete by ID -  errors/404 page")
    @Test
    void deleteByIdNotFound() throws Exception {
        Mockito.doReturn(false).when(vacancyService).deleteById(Mockito.anyInt());

        mvc.perform(get("/vacancies/delete/10"))
                .andExpect(view().name("errors/404"));

        Mockito.verify(vacancyService).deleteById(Mockito.anyInt());
    }

    @DisplayName("Update by ID -  errors/404 page")
    @Test
    void updateByIdNotFound() throws Exception {
        Vacancy expectedVacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);

        Mockito.doReturn(false).when(vacancyService).update(Mockito.any(), Mockito.any());

        var model = new ConcurrentModel();
        String view = vacancyController.update(expectedVacancy, testFile, model);

        Mockito.verify(vacancyService).update(Mockito.any(), Mockito.any());
        assertThat(view).isEqualTo("errors/404");
    }

    @DisplayName("Update by ID - OK")
    @Test
    void updateById() throws Exception {
        Vacancy expectedVacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);

        Mockito.doReturn(true).when(vacancyService).update(Mockito.any(), Mockito.any());

        var model = new ConcurrentModel();
        String view = vacancyController.update(expectedVacancy, testFile, model);

        Mockito.verify(vacancyService).update(Mockito.any(), Mockito.any());
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

}