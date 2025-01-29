package mako3.found.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mako3.found.mail.MailProperty;
import mako3.found.mail.MailPropertyService;
import mako3.found.mail.PasswordMailSenderService;

@Controller
public class AdminMailController {

    private static Log logger = LogFactory.getLog(AdminMailController.class);

    @Autowired
    private MailPropertyService mailPropertyService;

    @Autowired
    private PasswordMailSenderService mailSenderService;

    @GetMapping("/admin/mail")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminMail(Model model) {
        model.addAttribute("mail", mailPropertyService.getMailProperty());
        return "admin-mail";
    }

    @PostMapping("/admin/updateMailSettings")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateMailSettings(@Validated MailProperty mailProperty, BindingResult result,
            RedirectAttributes model) {
        if (!result.hasErrors()) {
            mailPropertyService.updateMailProperty(mailProperty);
            mailSenderService.init();
        }
        return "redirect:/admin/mail";
    }

}
