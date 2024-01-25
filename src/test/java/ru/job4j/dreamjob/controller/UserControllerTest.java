package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {
    @Mock
    HttpSession httpSession;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("Get login page")
    @Test
    void getLoginPage() throws Exception {
        mvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));
    }

    @DisplayName("POST login and password")
    @Test
    void loginUser() throws Exception {
        User expectedUser = new User(1, "test@test.ru", "name", "password");
        Mockito.doReturn(Optional.of(expectedUser)).when(userService).findByEmailAndPassword(Mockito.any(), Mockito.any());

        mvc.perform(post("/users/login")
                        .content("email=testName@email.ru&password=testPassword")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vacancies"));

        Mockito.verify(userService, Mockito.times(1)).findByEmailAndPassword(Mockito.any(), Mockito.any());

        Mockito.doReturn(httpSession).when(httpServletRequest).getSession();

        var model = new ConcurrentModel();
        userController.loginUser(expectedUser, model, httpServletRequest);

        Mockito.verify(httpSession, Mockito.times(1)).setAttribute("user", expectedUser);
    }

    @DisplayName("POST Bad login and password - 404 error")
    @Test
    void badLoginUserThenNotFound() throws Exception {
        Mockito.doReturn(Optional.empty()).when(userService).findByEmailAndPassword(Mockito.any(), Mockito.any());

        mvc.perform(post("/users/login")
                        .content("email=testName@email.ru&password=testPassword")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("error"));

        Mockito.verify(userService, Mockito.times(1)).findByEmailAndPassword(Mockito.any(), Mockito.any());
    }

    @DisplayName("Logout")
    @Test
    void logout() throws Exception {
        mvc.perform(get("/users/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/login"));

        userController.logout(httpSession);
        Mockito.verify(httpSession, Mockito.times(1)).invalidate();
    }

    @DisplayName("Get registration page")
    @Test
    void getRegistrationPage() throws Exception {
        mvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));
    }

    @DisplayName("POST Double email, Registration user error.")
    @Test
    void registerDoubleEmail() throws Exception {
        Mockito.doReturn(Optional.empty()).when(userService).findByEmailAndPassword(Mockito.any(), Mockito.any());

        mvc.perform(post("/users/register")
                        .content("email=testName@email.ru&name=name&password=testPassword")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeExists("error"));

        Mockito.verify(userService, Mockito.times(1)).save(Mockito.any());
    }

    @DisplayName("POST Registration user")
    @Test
    void register() throws Exception {
        User expectedUser = new User(1, "test@test.ru", "name", "password");
        Mockito.doReturn(Optional.of(expectedUser)).when(userService).save(Mockito.any());

        mvc.perform(post("/users/register")
                        .content("id=" + expectedUser.getId()
                                + "&email=" + expectedUser.getEmail()
                                + "&name=" + expectedUser.getName()
                                + "&password=" + expectedUser.getPassword())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", expectedUser));

        Mockito.verify(userService, Mockito.times(1)).save(Mockito.any());
    }

}