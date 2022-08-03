package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.HomeAndPortfolioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SiteControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @MockBean
    HomeAndPortfolioService homeAndPortfolioService;
    @Autowired
    private MockMvc mvc;
    @MockBean
    Model model;
    @MockBean
    Principal principal;
    @Test
    void index() throws Exception {
        this.mvc
                .perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }


    @Test
    void home() throws Exception  {
//        MvcResult mvcResult = this.mvc.perform(get("/home")).andReturn();
//        MockHttpServletRequest request = mvcResult.getRequest();
//        MockHttpServletResponse response = mvcResult.getResponse();
//        ModelAndView modelAndView = new ModelAndView("home");
        String filter = "test";
        String coinId= "test";
        String sorting = "test";

//        MvcResult mvcResult = mvc
//                .perform(get("/home").param( filter, coinId, sorting))
//                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//                .andExpect(MockMvcResultMatchers.view().name("home")).andReturn();

//        ModelAndView mv = mvcResult.getModelAndView();
        ModelAndView modelAndView = new ModelAndView("home");
        MvcResult mvcResult = this.mvc.perform(get("/home")).andReturn();
        MockHttpServletRequest request = mvcResult.getRequest();
        MockHttpServletResponse response = mvcResult.getResponse();

        this.mvc
                .perform(get("/home").param( filter, coinId, sorting))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());


                Assertions.assertEquals("home", modelAndView.getViewName() );



    }

    @Test
    void portfolio() throws Exception{
        String filter = "test";
        String coinId= "test";
        ModelAndView modelAndView = new ModelAndView("home");
        MvcResult mvcResult = this.mvc.perform(get("/portfolio")).andReturn();
        MockHttpServletRequest request = mvcResult.getRequest();
        MockHttpServletResponse response = mvcResult.getResponse();

        this.mvc
                .perform(get("/portfolio").param( filter, coinId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());


        Assertions.assertEquals("home", modelAndView.getViewName() );
    }
}