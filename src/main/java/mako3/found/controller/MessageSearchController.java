package mako3.found.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mako3.found.entity.ChatMessage;
import mako3.found.service.MessageService;

@Controller
public class MessageSearchController {

    @Autowired
    private MessageService service;

    @GetMapping(value = "/messages")
    public String findMessages(
            @CurrentSecurityContext SecurityContext context,
            @RequestParam(name = "url", required = false) String url,
            @RequestParam(name = "messageText", required = false) String messageText,
            @RequestParam(name = "spaceId", required = false) String spaceId,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            Model model) {

        model.addAttribute("queryType", "messageText");
        if (url != null && !url.isEmpty()) {
            long t1 = System.currentTimeMillis();
            List<ChatMessage> messages = service.findByUrl(url);
            model.addAttribute("queryType", "url");
            model.addAttribute("keyword", url);
            model.addAttribute("messageList", messages);
            long t2 = System.currentTimeMillis();
            model.addAttribute("queryTime", t2 - t1);
        }
        if (messageText != null && !messageText.isEmpty()) {
            long t1 = System.currentTimeMillis();
            List<ChatMessage> messages = service.find(messageText, limit);
            model.addAttribute("queryType", "messageText");
            model.addAttribute("keyword", messageText);
            model.addAttribute("messageList", messages);
            long t2 = System.currentTimeMillis();
            model.addAttribute("queryTime", t2 - t1);
        }

        return "message-search";
    }

}
