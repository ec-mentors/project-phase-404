package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.data.YieldData;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.YieldCalculatorService;
import org.springframework.beans.factory.annotation.Value;
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
    private final String errorMessage;
    public YieldCalculatorController(YieldCalculatorService yieldCalculatorService, UserService userService,
                                     @Value("${messages.yieldcalculator.noprofile}")String errorMessage) {

        this.yieldCalculatorService = yieldCalculatorService;
        this.userService = userService;
        this.errorMessage = errorMessage;
    }

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

        var zero = "0";

        if (!user.getAssetsAllocation().equals("none")) {
            if (!days.equals(zero)) {
                if (monthlyAmount.isBlank()) {
                    monthlyAmount = zero;
                }
                if (period.isBlank()) {
                    period = zero;
                }

                yieldCalculatorService.setAttributesWithMovingAverage(period, monthlyAmount,
                        model, principal, Integer.parseInt(days));

                model.addAttribute("days", days);

                return "calculator";


            } else {
                if (monthlyAmount.isBlank()) {
                    monthlyAmount = zero;
                }
                if (period.isBlank()) {
                    period = zero;
                }
                yieldCalculatorService.checkParameters(monthlyAmount, period);
                yieldCalculatorService.setAttributes(period, monthlyAmount, model, principal);
                return "calculator";
            }
        }
        model.addAttribute("yieldcalculator",errorMessage);
        return "calculator";
    }
}
