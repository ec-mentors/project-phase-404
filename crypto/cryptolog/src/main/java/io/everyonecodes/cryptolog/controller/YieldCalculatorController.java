package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.*;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.UserServiceImp;
import io.everyonecodes.cryptolog.service.YieldCalculatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class YieldCalculatorController {
    private final UserServiceImp userServiceImp;
    private final YieldCalculatorService yieldCalculatorService;
    private final UserService userService;
    private final CoingeckoClient client;

    public YieldCalculatorController(UserServiceImp userServiceImp, YieldCalculatorService yieldCalculatorService, UserService userService, CoingeckoClient client) {
        this.userServiceImp = userServiceImp;
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
        if (monthlyAmount == null || period == null || days == null){
            return "calculator";
        }

        List<YieldData> yieldDataList = new ArrayList<>();
        User user = userService.loadLoggedInUser(principal);


        if(!user.getAssetsAllocation().equals("none")) {

            if (days == null || days.isBlank()) {
                days = "0";
            }
            if (!days.equals("0")) {

                //System.out.println("days 0");
                return "calculatorlock";

            } else {
                if (monthlyAmount.isBlank()) {
                    monthlyAmount = "0";
                }
                if (period.isBlank()) {
                    period = "0";
                }
                yieldCalculatorService.checkParameters(monthlyAmount, period);
                yieldCalculatorService.setAttributes(period, monthlyAmount, model, principal);
                model.addAttribute("day", days);
             //   System.out.println(days);
//            System.out.println(user.getDay());
//            System.out.println(day.getDay());
                return "calculator";
            }
        }

        return "calculatorlock";
    }
//    @PostMapping(value = "/calculator")
//    public String updateDay(Principal principal, @ModelAttribute("day")
//            Day day , Model model,BindingResult bindingResult) {
//        User user = userService.loadLoggedInUser(principal);
//        user.setDay(day.getDay());
//        userServiceImp.save(user);
//        model.addAttribute("assetMessage", "Missing coin from Tier 2 or 3. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");
//        System.out.println("post " + day.getDay());
//        System.out.println("post " + user.getDay());
//        if (bindingResult.hasErrors()){
//            System.out.println("error");
//        }
//
//    return "calculator";
//
//    }



}
