package mako3.found.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mako3.found.auth.CustomUserDetails;
import mako3.found.auth.CustomUserDetailsService;
import mako3.found.mail.PasswordMailSenderService;

@Controller
public class AdminUsersController {

    private static Log logger = LogFactory.getLog(AdminUsersController.class);

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private PasswordMailSenderService mailService;

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsers(Model model) {
        List<CustomUserDetails> userList = userService.loadAllUsers();
        model.addAttribute("userList", userList);
        model.addAttribute("userCount", userList.size());
        return "admin-users";
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(@CurrentSecurityContext SecurityContext context,
            @RequestParam("username") String username) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        try {
            CustomUserDetails targetUser = (CustomUserDetails) userService.loadUserByUsername(username);
            String newPassword = userService.resetPassword(username);
            mailService.sendMail(targetUser.getEmailForNotification(), username, newPassword);
        } catch (UsernameNotFoundException e) {
            logger.error(String.format("the user is not registered : %s", username));
            return "failed";
        }
        return "succeeded";
    }

}
