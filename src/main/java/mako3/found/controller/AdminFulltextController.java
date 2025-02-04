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
import mako3.found.service.FulltextSettingsService;

@Controller
public class AdminFulltextController {

    private static Log logger = LogFactory.getLog(AdminFulltextController.class);

    @Autowired
    private FulltextSettingsService fulltextService;

    @GetMapping("/admin/fulltext")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminFulltext(Model model) {
        model.addAttribute("fulltextEnabled", fulltextService.isFulltextEnabled() ? "enabled" : "disabled");
        return "admin-fulltext";
    }

    @PostMapping("/admin/updateFulltextSettings")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateFulltextSettings(@CurrentSecurityContext SecurityContext context,
            @RequestParam("fulltext-enabled") String strEnabled,
            RedirectAttributes model) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        boolean enabled = "enabled".equals(strEnabled);
        fulltextService.updateFulltextEnabled(enabled);
        logger.info(String.format("Succeeded to update fulltext settings by %s", user.getUsername()));
        model.addFlashAttribute("successMessage", "インデックス設定を更新しました。");
        return "redirect:/admin/fulltext";
    }

}
