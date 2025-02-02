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
import mako3.found.service.NoticeService;

@Controller
public class AdminNoticeController {

    private static Log logger = LogFactory.getLog(AdminNoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/admin/notice")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminNotice(Model model) {
        model.addAttribute("notice", noticeService.getNotice());
        return "admin-notice";
    }

    @PostMapping("/admin/updateNotice")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateNotice(@CurrentSecurityContext SecurityContext context, @RequestParam("notice") String notice,
            RedirectAttributes model) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        noticeService.updateNotice(notice);
        logger.info(String.format("Succeeded to update notice by %s", user.getUsername()));
        model.addFlashAttribute("successMessage", "お知らせ設定を更新しました。");
        return "redirect:/admin/notice?success";
    }

}
