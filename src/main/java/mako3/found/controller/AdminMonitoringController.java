package mako3.found.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mako3.found.auth.CustomUserDetails;
import mako3.found.service.MonitoringSnippetService;

@Controller
public class AdminMonitoringController {

    private static Log logger = LogFactory.getLog(AdminMonitoringController.class);

    @Autowired
    private MonitoringSnippetService snippetService;

    @GetMapping("/admin/monitoring")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminMonitoring(Model model) {
        model.addAttribute("snippet", snippetService.getSnippet());
        return "admin-monitoring";
    }

    @PostMapping("/admin/updateSnippet")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSnippet(@CurrentSecurityContext SecurityContext context,
            @RequestParam("snippet") String snippet,
            RedirectAttributes model) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        snippetService.updateSnippet(snippet);
        logger.info(String.format("Succeeded to update monitoring snippet by %s", user.getUsername()));
        return "redirect:/admin/monitoring";
    }

}
