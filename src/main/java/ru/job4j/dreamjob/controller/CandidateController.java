package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.impl.CandidateServiceImpl;

/**
 * Работать с кандидатами будем по URI /candidates/**
 */
@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService = CandidateServiceImpl.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Candidate candidate) {
        candidateService.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var candidateOptional = candidateService.findById(id);
        if (candidateOptional.isEmpty()) {
            return sendNotFoundError(model);
        }
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        boolean isUpdated = candidateService.update(candidate);
        if (!isUpdated) {
            return sendNotFoundError(model);
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            return sendNotFoundError(model);
        }
        return "redirect:/candidates";
    }

    private static String sendNotFoundError(Model model) {
        model.addAttribute("message", "Кандидат с указанным идентификатором не найден");
        return "errors/404";
    }
}