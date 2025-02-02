package mako3.found.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mako3.found.auth.CustomUserDetails;
import mako3.found.auth.CustomUserDetailsService;
import mako3.found.auth.PasswordPolicyViolationException;

@Controller
public class ChangePasswordController {

    private static Log logger = LogFactory.getLog(AdminUsersController.class);

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/user/password/change")
    public String showChangePassword() {
        return "change-password";
    }

    @PostMapping("/user/password/update")
    public String changePassword(@CurrentSecurityContext SecurityContext context,
            @RequestParam("password") String newPassword, RedirectAttributes model) {

        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        try {
            userService.updatePassword(user.getUsername(), newPassword, false);
            logger.info(String.format("succeeded to update password for %s", user.getUsername()));
        } catch (PasswordPolicyViolationException e) {
            // if invalid, redirect with error message
            model.addFlashAttribute("failureMessage", e.getMessage());
            return "redirect:/user/password/change?error";
        }

        // redirect to home
        return "redirect:/";
    }

}
