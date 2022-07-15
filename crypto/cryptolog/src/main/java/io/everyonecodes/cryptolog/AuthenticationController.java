package io.everyonecodes.cryptolog;

import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.EmailSenderService;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class AuthenticationController {
    @Autowired
    UserService userService;
    @Autowired
    EmailSenderService emailSenderService;

//    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
//    public ModelAndView login() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("login"); // resources/template/login.html
//        return modelAndView;
//    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

//    @RequestMapping(value = "/register", method = RequestMethod.GET)
//    public ModelAndView register() {
//        ModelAndView modelAndView = new ModelAndView();
//        User user = new User();
//        modelAndView.addObject("user", user);
//        modelAndView.setViewName("register"); // resources/template/register.html
//        return modelAndView;
//    }

    @GetMapping("/register")
    String register(Model model) {
        User user = new User();
        model.addAttribute(user);
        return "register";
    }

//    @RequestMapping(value = "/home", method = RequestMethod.GET)
//    public ModelAndView home() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("home"); // resources/templates/home.html
//        return modelAndView;
//    }

    @GetMapping("/home")
    String home() {
        return "home";
    }

    //@RequestMapping(value = "/register", method = RequestMethod.POST)
    @PostMapping("/register")
    public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("successMessage", "Please write the fields in the right format!");
            modelMap.addAttribute("bindingResult", bindingResult);
        } else if (userService.isUserAlreadyPresent(user)) {
            modelAndView.addObject("successMessage", "User already exists!");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User is registered successfully, please verify your email. <a href=\"/login\">Login</a>");
           // modelAndView.setViewName("firstlogin");
            emailSenderService.sendEmail(user);
        }
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("register");
        return modelAndView;
    }

}
