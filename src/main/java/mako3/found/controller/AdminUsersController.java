package mako3.found.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mako3.found.auth.CustomUserDetails;
import mako3.found.auth.CustomUserDetailsService;
import mako3.found.entity.NewUser;
import mako3.found.mail.MailSendingException;

@Controller
public class AdminUsersController {

    private static Log logger = LogFactory.getLog(AdminUsersController.class);

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsers(Model model) {
        List<CustomUserDetails> userList = userService.loadAllUsers();
        model.addAttribute("userList", userList);
        model.addAttribute("userCount", userList.size());
        return "admin-users";
    }

    @PostMapping("/admin/resetPassword")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> resetPassword(@CurrentSecurityContext SecurityContext context,
            @RequestParam("username") String username) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        try {
            CustomUserDetails targetUser = (CustomUserDetails) userService.loadUserByUsername(username);
            userService.resetPasswordWithMail(username, targetUser.getEmailForNotification());
        } catch (MailSendingException e) {
            logger.error(String.format("failed to reset password for %s", username), e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("success");
    }

    @PostMapping("/admin/addUser")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addUser(@CurrentSecurityContext SecurityContext context, @Validated NewUser newUser,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input.");
        }

        try {
            userService.addUser(newUser);
        } catch (DuplicateKeyException e) {
            logger.error(String.format("failed to add user %s for duplicate key", newUser.getUsername()), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("usernaeme / email for notification should be unique.");
        }
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/admin/deleteUser")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestParam("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("success");
    }

}
