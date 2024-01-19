package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.VacancyService;

/**
 * Работать с вакансиями будем по URI /vacancies/**
 */
@Controller
@RequestMapping("/vacancies") /* Работать с кандидатами будем по URI /vacancies/** */
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    private static String sendNotFoundError(Model model) {
        model.addAttribute("message", "Вакансия с указанным идентификатором не найдена");
        return "errors/404";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Vacancy vacancy) {
        vacancyService.save(vacancy);
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isEmpty()) {
            return sendNotFoundError(model);
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        boolean isUpdated = vacancyService.update(vacancy);
        if (!isUpdated) {
            return sendNotFoundError(model);
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            return sendNotFoundError(model);
        }
        return "redirect:/vacancies";
    }
}