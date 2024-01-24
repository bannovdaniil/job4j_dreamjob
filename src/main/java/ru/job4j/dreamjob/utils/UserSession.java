package ru.job4j.dreamjob.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;

public final class UserSession {
    private UserSession() {
    }

    public static User setUserFromSession(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
            session.setAttribute("user", user);
        }
        model.addAttribute("user", user);
        return user;
    }
}
