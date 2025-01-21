package mako3.found.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mako3.found.service.FulltextSettingsService;

@Controller
public class AdminFulltextController {

    private static Log logger = LogFactory.getLog(AdminFulltextController.class);

    @Autowired
    private FulltextSettingsService fulltextService;

    @GetMapping("/admin/fulltext")
    public String adminFulltext(Model model) {
        model.addAttribute("fulltextEnabled", fulltextService.isFulltextEnabled() ? "enabled" : "disabled");
        return "admin-fulltext";
    }

    @PostMapping("/admin/updateFulltextSettings")
    public String updateFulltextSettings(@RequestParam("fulltext-enabled") String strEnabled, Model model) {
        boolean enabled = "enabled".equals(strEnabled);
        if (enabled != fulltextService.isFulltextEnabled()) {
            fulltextService.updateFulltextEnabled(enabled);
        }
        return "redirect:/admin/fulltext";
    }

}
