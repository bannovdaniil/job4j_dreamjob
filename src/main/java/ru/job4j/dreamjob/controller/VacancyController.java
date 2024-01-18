package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.VacancyService;
import ru.job4j.dreamjob.service.impl.VacancyServiceImpl;

/**
 * Работать с вакансиями будем по URI /vacancies/**
 */
@Controller
@RequestMapping("/vacancies") /* Работать с кандидатами будем по URI /vacancies/** */
public class VacancyController {

    private final VacancyService vacancyRepository = VacancyServiceImpl.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyRepository.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Vacancy vacancy) {
        vacancyRepository.save(vacancy);
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var vacancyOptional = vacancyRepository.findById(id);
        if (vacancyOptional.isEmpty()) {
            return sendNotFoundError(model);
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        boolean isUpdated = vacancyRepository.update(vacancy);
        if (!isUpdated) {
            return sendNotFoundError(model);
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = vacancyRepository.deleteById(id);
        if (!isDeleted) {
            return sendNotFoundError(model);
        }
        return "redirect:/vacancies";
    }

    private static String sendNotFoundError(Model model) {
        model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
        return "errors/404";
    }
}