package mako3.found.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import mako3.found.auth.CustomUserDetails;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.ChatSpace;
import mako3.found.service.MessageService;
import mako3.found.service.SpaceService;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SpaceService spaceService;

    @GetMapping(value = "/{spaceId}/messages")
    public String message(
            @CurrentSecurityContext SecurityContext context,
            @PathVariable String spaceId,
            @RequestParam(name = "dateFrom", required = false) String dateFrom,
            @RequestParam(name = "dateBefore", required = false) String dateBefore,
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            Model model) {

        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        if (dateFrom == null && dateBefore == null) {
            List<ChatMessage> messages = messageService.list(user, spaceId, limit + 1);
            ChatSpace space = spaceService.getOneCached(spaceId);
            model.addAttribute("space", space);
            model.addAttribute("messageList", messages);
            if (messages.size() > limit) {
                model.addAttribute("messageList", messages.subList(0, Math.min(limit, messages.size())));
                model.addAttribute("dateFrom", messages.get(limit).getUrlSafeCreatedDate());
            }
        } else if (dateFrom != null) {
            LocalDateTime datetimeFrom = LocalDateTime.parse(dateFrom, ChatMessage.FORMATTER);
            List<ChatMessage> messages = messageService.listFrom(user, spaceId, datetimeFrom, limit + 1);
            ChatSpace space = spaceService.getOneCached(spaceId);
            model.addAttribute("space", space);
            model.addAttribute("messageList", messages.subList(0, Math.min(limit, messages.size())));
            if (messages.size() > limit) {
                model.addAttribute("dateFrom", messages.get(limit).getUrlSafeCreatedDate());
            }
            if (!messages.isEmpty()) {
                model.addAttribute("dateBefore", messages.get(0).getUrlSafeCreatedDate());
            }

        } else if (dateBefore != null) {
            LocalDateTime datetimeBefore = LocalDateTime.parse(dateBefore, ChatMessage.FORMATTER);
            List<ChatMessage> messages = messageService.listBefore(user, spaceId, datetimeBefore, limit + 1);
            ChatSpace space = spaceService.getOneCached(spaceId);
            model.addAttribute("space", space);
            model.addAttribute("messageList", messages.subList(0, Math.min(limit, messages.size())));
            if (messages.size() > limit) {
                model.addAttribute("dateBefore", messages.get(0).getUrlSafeCreatedDate());
            }
            if (!messages.isEmpty()) {
                model.addAttribute("dateFrom", dateBefore);
            }
        }
        return "message";
    }

}
