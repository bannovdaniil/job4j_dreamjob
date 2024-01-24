package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.dreamjob.utils.UserSession;

@Controller
@ThreadSafe
public class IndexController {
    @GetMapping({"/", "/index"})
    public String getIndex(Model model, HttpSession session) {
        UserSession.setUserFromSession(model, session);
        return "index";
    }

}
