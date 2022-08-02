package io.everyonecodes.cryptolog.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import java.security.Principal;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class YieldCalculatorControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @MockBean
     YieldCalculatorService yieldCalculatorService;
    @MockBean
    UserService userService;
    @MockBean
    SecurityFilterChain securityFilterChain;
    @MockBean
    Model model;
    @MockBean
    Principal principal;

    @Autowired
    private MockMvc mvc;

@Test
void returnView () throws Exception {
    String monthlyAmount ="test";
    String period = "test";
    String days = "1";
    yieldCalculatorService.setAttributesWithMovingAverage(period, monthlyAmount,
            model, principal, Integer.parseInt(days));
    this.mvc
            .perform(MockMvcRequestBuilders.get("/calculator"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("calculator"));

}



    @Test
    void getYieldResultsNone() {

        String monthlyAmount ="test";
        String period = "test";
        String days = "1";
        String url = "/calculator";
        User user = new User("test", "test", "test");
        Mockito.when(userService.loadLoggedInUser(principal)).thenReturn(user);
        user.setAssetsAllocation("none");
        restTemplate.getForObject(url, String.class);

    }
    @Test
    void getYieldResults() {

        String monthlyAmount ="test";
        String period = "test";

        String url = "/calculator";
        User user = new User("test", "test", "test");
        Mockito.when(userService.loadLoggedInUser(principal)).thenReturn(user);
        user.setAssetsAllocation("Gambler");
        restTemplate.getForObject(url, String.class);



    }

}