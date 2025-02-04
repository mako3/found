package mako3.found.controller;

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
            @RequestParam(name = "seqFrom", required = false, defaultValue = "1") int seqFrom,
            @RequestParam(name = "seqTo", required = false, defaultValue = "100") int seqTo,
            Model model) {

        if (seqFrom < 1 || seqTo < 1 || seqFrom > seqTo) {
            return "redirect:/error";
        }

        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        int limit = seqTo - seqFrom + 1;
        List<ChatMessage> messages = messageService.list(user, spaceId, seqFrom, limit + 1);
        ChatSpace space = spaceService.getOneCached(spaceId);
        model.addAttribute("space", space);

        boolean hasPrev = seqFrom > 1 ? true : false;
        boolean hasNext = messages.size() > limit ? true : false;

        if (hasNext && hasPrev) {
            model.addAttribute("messageList", messages.subList(0, limit));
            model.addAttribute("prev", true);
            model.addAttribute("next", true);
            model.addAttribute("seqPrevFrom", Math.max(seqFrom - 100, 1));
            model.addAttribute("seqPrevTo", seqFrom - 1);
            model.addAttribute("seqNextFrom", seqTo + 1);
            model.addAttribute("seqNextTo", seqTo + 100);
        } else if (hasNext) {
            model.addAttribute("messageList", messages.subList(0, limit));
            model.addAttribute("prev", false);
            model.addAttribute("next", true);
            model.addAttribute("seqNextFrom", seqTo + 1);
            model.addAttribute("seqNextTo", seqTo + 100);
        } else if (hasPrev) {
            model.addAttribute("messageList", messages);
            model.addAttribute("prev", true);
            model.addAttribute("next", false);
            model.addAttribute("seqPrevFrom", Math.max(seqFrom - 100, 1));
            model.addAttribute("seqPrevTo", seqFrom - 1);
        } else {
            model.addAttribute("messageList", messages);
            model.addAttribute("prev", false);
            model.addAttribute("next", false);
        }

        return "message";
    }

}
