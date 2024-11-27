package mako3.found.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping(value = "/login", params = "failure")
    public String loginFail(Model model) {
        model.addAttribute("failureMessage", "ログインに失敗しました");
        return "login";
    }

    @GetMapping("/display-access-denied")
    public String accessDenied() {
        return "access-denial";
    }

}
