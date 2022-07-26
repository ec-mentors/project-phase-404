//package io.everyonecodes.cryptolog.controller;
//
//import io.everyonecodes.cryptolog.CoingeckoClient;
//import io.everyonecodes.cryptolog.data.Coin;
//import io.everyonecodes.cryptolog.data.User;
//import io.everyonecodes.cryptolog.data.YieldData;
//import io.everyonecodes.cryptolog.service.UserService;
//import io.everyonecodes.cryptolog.service.YieldCalculatorService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.math.BigDecimal;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//public class YieldCalculatorController {
//    private final CoingeckoClient client;
//    private final UserService userService;
//    private final YieldCalculatorService yieldCalculatorService;
//
//    public YieldCalculatorController(CoingeckoClient client, UserService userService, YieldCalculatorService yieldCalculatorService) {
//        this.client = client;
//        this.userService = userService;
//        this.yieldCalculatorService = yieldCalculatorService;
//    }
//
//    @GetMapping("/calculator")
//    public List<YieldData> getCalculator(Model model,
//                                         @RequestBody() String monthlyAmount,
//                                         @RequestBody() String period,
//                                         Principal principal) {
//        List<YieldData> yieldDataList = new ArrayList<>();
//        User user = userService.loadLoggedInUser(principal);
//        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
//        for (Coin coin:coinList){
//            yieldDataList.add(yieldCalculatorService.calculate(coin.getId(),
//                    BigDecimal.valueOf(Long.parseLong((monthlyAmount))),
//                    BigDecimal.valueOf(coin.getCurrent_price()),
//                    BigDecimal.valueOf(coin.getAth()),
//                    Integer.parseInt(period)));
//        }
//        model.addAttribute("")
//    return yieldDataList;
//
//    }
//}
