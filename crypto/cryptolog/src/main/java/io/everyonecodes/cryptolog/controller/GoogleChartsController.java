package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.GoogleChartsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleChartsController {
    
    private final GoogleChartsService googleChartsService;
    
    public GoogleChartsController(GoogleChartsService googleChartsService) {
        this.googleChartsService = googleChartsService;
    }
    
    @GetMapping("/chart")
    public String getPieChart(Model model) {
        return googleChartsService.getChart(model);
    }
}