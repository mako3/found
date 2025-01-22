package mako3.found.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SelftPasswordResetController {

    @GetMapping("/self-password-reset")
    public String selfPasswordReset() {
        return "password-reset";
    }

}
