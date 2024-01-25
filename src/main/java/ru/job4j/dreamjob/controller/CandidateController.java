package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;

/**
 * Работать с кандидатами будем по URI /candidates/**
 */
@Controller
@ThreadSafe
@RequestMapping("/candidates")
public class CandidateController {
    private static final String NOT_FOUND_CANDIDATE_MESSAGES = "Кандидат с указанным идентификатором не найден";

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    private static String sendNotFoundError(Model model, String message) {
        model.addAttribute("message", message);
        return "errors/404";
    }

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
    public String createPost(@ModelAttribute Candidate candidate, Model model) {
        try {
            candidateService.save(candidate);
            return "redirect:/candidates";
        } catch (Exception exception) {
            return sendNotFoundError(model, exception.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var candidateOptional = candidateService.findById(id);
        if (candidateOptional.isEmpty()) {
            return sendNotFoundError(model, NOT_FOUND_CANDIDATE_MESSAGES);
        }
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        boolean isUpdated = candidateService.update(candidate);
        if (!isUpdated) {
            return sendNotFoundError(model, NOT_FOUND_CANDIDATE_MESSAGES);
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            return sendNotFoundError(model, NOT_FOUND_CANDIDATE_MESSAGES);
        }
        return "redirect:/candidates";
    }
}