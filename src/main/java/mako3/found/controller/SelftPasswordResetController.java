package mako3.found.controller;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mako3.found.auth.CustomUserDetailsService;
import mako3.found.entity.PasswordResetToken;
import mako3.found.mail.MailSendingException;
import mako3.found.security.SecurityConfig;

/**
 * all endpoints are not secured (exclusion of authentication)
 * @see SecurityConfig
 */
@Controller
public class SelftPasswordResetController {

    private static Log logger = LogFactory.getLog(SelftPasswordResetController.class);

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/pw-reset/request")
    public String showRequestForm(Model model) {
        return "password-reset-request";
    }

    @PostMapping("/pw-reset/send-token")
    public String sendTokenIfEmailExists(@RequestParam("email") String email, RedirectAttributes model) {
        // if email exists, send token to email
        Optional<String> username = userService.resolveUsernameIfExists(email);
        if (username.isPresent()) {
            try {
                userService.recordTokenWithMail(username.get(), email);
            } catch (MailSendingException e) {
                model.addFlashAttribute("failureMessage", "トークンの送信に失敗しました。管理者にお問い合わせください。");
            }
        }

        // no matter how email exists, show sent message for security
        model.addFlashAttribute("successMessage", "パスワード再発行の要求を受け付けました。メールをご確認ください。");
        return "redirect:/pw-reset/request";
    }

    @GetMapping("/pw-reset/open-link")
    public String openLinkFromMail(@RequestParam("token") String token, Model model) {
        // if token is valid, show form for update. Otherwise, show expired message
        Optional<PasswordResetToken> resetToken = userService.findToken(token);
        if (resetToken.isPresent()) {
            model.addAttribute("token", token);
            return "password-reset";
        } else {
            return "redirect:/pw-reset/invalid-token";
        }
    }

    @GetMapping("/pw-reset/invalid-token")
    public String showResetInvalid() {
        return "password-reset-invalid";
    }

    @Transactional
    @PostMapping("/pw-reset/reset")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword,
            RedirectAttributes model) {
        Optional<PasswordResetToken> resetToken = userService.findToken(token);
        if (newPassword.length() < 8) {
            model.addFlashAttribute("failureMessage", "パスワードは入力規則を満たしていません。");
            return "redirect:/pw-reset/open-link?token=" + token;
        }

        if (resetToken.isPresent()) {
            // do password update
            userService.updatePassword(resetToken.get().getUsername(), newPassword);
            userService.invalidateToken(token);
            logger.info(String.format("succeeded to update password for %s", resetToken.get().getUsername()));
            return "redirect:/pw-reset/reset-completed";
        } else {
            return "redirect:/pw-reset/invalid-token";
        }
    }

    @GetMapping("/pw-reset/reset-completed")
    public String showResetCompleted() {
        return "password-reset-completed";
    }

}
