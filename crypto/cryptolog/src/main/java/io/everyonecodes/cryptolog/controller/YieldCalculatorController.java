package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.data.YieldData;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.YieldCalculatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class YieldCalculatorController {
    
    private final YieldCalculatorService yieldCalculatorService;
    private final UserService userService;
    private final CoingeckoClient client;
    
    public YieldCalculatorController( YieldCalculatorService yieldCalculatorService, UserService userService, CoingeckoClient client) {
        
        this.yieldCalculatorService = yieldCalculatorService;
        this.userService = userService;
        this.client = client;
    }
    
    private String test;
    
    @GetMapping("/calculator")
    public String getYieldResults(Model model,
                                  @RequestParam(required = false) String monthlyAmount,
                                  @RequestParam(required = false) String period,
                                  @RequestParam(required = false) String days,
                                  Principal principal) {
        if (monthlyAmount == null || period == null || days == null) {
            return "calculator";
        }
        
        List<YieldData> yieldDataList = new ArrayList<>();
        User user = userService.loadLoggedInUser(principal);
        
        
        if (!user.getAssetsAllocation().equals("none")) {
            if (!days.equals("0")) {
                if (monthlyAmount.isBlank()) {
                    monthlyAmount = "0";
                }
                if (period.isBlank()) {
                    period = "0";
                }
                
                yieldCalculatorService.setAttributesWithMovingAverage(period, monthlyAmount,
                                                                      model, principal, Integer.parseInt(days));
                
                model.addAttribute("days", days);
                
                return "calculator";
                
            } else {
                if (monthlyAmount.isBlank()) {
                    monthlyAmount = "0";
                }
                if (period.isBlank()) {
                    period = "0";
                }
                yieldCalculatorService.checkParameters(monthlyAmount, period);
                yieldCalculatorService.setAttributes(period, monthlyAmount, model, principal);
                return "calculator";
            }
        }
        
        return "calculatorlock";
    }
}
