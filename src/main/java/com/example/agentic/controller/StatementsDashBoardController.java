package com.example.agentic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.agentic.repository.ValidationResultRepository;

@Controller
public class StatementsDashBoardController {
	
	@Autowired
    private ValidationResultRepository logRepo;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("logs", logRepo.findAll());
        return "dashboard";
    }
	
}
