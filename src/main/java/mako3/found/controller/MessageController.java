package mako3.found.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
            @PathVariable String spaceId,
            @RequestParam(name = "topicId", required = false) String messageText,
            Model model) {
        List<ChatMessage> messages = messageService.list(spaceId, 100);
        ChatSpace space = spaceService.findOne(spaceId);
        model.addAttribute("space", space);
        model.addAttribute("messageList", messages);
        return "message";
    }

}
