package mako3.found.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestParam(name = "url", required = false) String url,
            @RequestParam(name = "messageText", required = false) String messageText,
            @RequestParam(name = "spaceId", required = false) String spaceId,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "limit", required = false) String limit,
            Model model) {

        model.addAttribute("queryType", "messageText");
        if (url != null && !url.isEmpty()) {
            List<ChatMessage> messages = service.findByUrl(url);
            model.addAttribute("queryType", "url");
            model.addAttribute("keyword", url);
            model.addAttribute("messageList", messages);
        }
        if (messageText != null && !messageText.isEmpty()) {
            List<ChatMessage> messages = service.find(messageText);
            model.addAttribute("queryType", "messageText");
            model.addAttribute("keyword", messageText);
            model.addAttribute("messageList", messages);
        }
        return "message-search";
    }

}
