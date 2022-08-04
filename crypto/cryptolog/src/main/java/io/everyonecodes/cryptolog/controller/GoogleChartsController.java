package io.everyonecodes.cryptolog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleChartsController {
    
    private final GoogleChartsController googleChartsController;
    
    public GoogleChartsController(GoogleChartsController googleChartsController) {
        this.googleChartsController = googleChartsController;
    }
    
    @GetMapping("/chart")
    public String getPieChart(Model model) {
        return googleChartsController.getPieChart(model);
    }
}