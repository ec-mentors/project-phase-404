package io.everyonecodes.cryptolog.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTest {
	
	@Autowired
	TestRestTemplate testRestTemplate;

//	@URL(Url)
	String url = "/login";
//	@MockBean
//	LoginService loginService;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean Model model;
	
	
//	@Test
//	void loginRequest() {
//		String url = "/login";
//		String result = testRestTemplate.getForObject(url, String.class);
//
//		Assertions.assertEquals("login", result);
//
//		Mockito.verify(loginService).getLoggedIn(Mockito.any(HttpServletRequest.class), Mockito.any(Model.class));
//	}
	
	@Test
	void login() throws Exception {

//		HttpServletRequest request = mock(HttpServletRequest.class);
//		HttpServletResponse response = mock(HttpServletResponse.class);
//
//		when(request.getHeader("login")).thenReturn("success");
//
//		servlet.doGet(request, response);
//
//		assertThat(writer.toString()).isEqualTo("Full Name: Mockito Test");
		
		ModelAndView modelAndView = new ModelAndView("login");

		MvcResult mvcResult = this.mvc.perform(get(url)).andReturn();
		MockHttpServletRequest result = mvcResult.getRequest();
		MockHttpServletResponse response = mvcResult.getResponse();


		this.mvc.perform(get(url)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("login"));


		Assertions.assertEquals("login", modelAndView.getViewName());
	}
}