package mako3.found.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String updateNotice(@RequestParam("notice") String notice, RedirectAttributes model) {
        noticeService.updateNotice(notice);
        return "redirect:/admin/notice";
    }

}
