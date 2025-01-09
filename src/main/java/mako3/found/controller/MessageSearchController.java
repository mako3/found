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

import io.micrometer.common.util.StringUtils;
import mako3.found.auth.CustomUserDetails;
import mako3.found.entity.ChatMessage;
import mako3.found.entity.MessageQuery;
import mako3.found.entity.MessageQuery.QueryScope;
import mako3.found.entity.MessageQuery.QueryType;
import mako3.found.service.MessageService;

@Controller
public class MessageSearchController {

    @Autowired
    private MessageService service;

    @GetMapping(value = "/messages")
    public String findMessages(
            @CurrentSecurityContext SecurityContext context,
            @RequestParam(name = "queryType", required = false) String queryType,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "spaceId", required = false) String spaceId,
            @RequestParam(name = "creatorEmail", required = false) String creatorEmail,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "queryScope", required = false) String queryScope,
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            Model model) {

        if (!StringUtils.isEmpty(queryType)) {
            CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
            MessageQuery query = MessageQuery.builder()
                    .queryType(QueryType.valueOf(queryType))
                    .keyword(keyword)
                    .spaceId(spaceId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .queryScope(QueryScope.valueOf(queryScope))
                    .creatorEmail(creatorEmail)
                    .limit(limit)
                    .build();

            long t1 = System.currentTimeMillis();
            List<ChatMessage> messages = service.find(user, query);
            long t2 = System.currentTimeMillis();
            model.addAttribute("queryTime", t2 - t1);
            model.addAttribute("messageList", messages);
            model.addAttribute("queryType", query.getQueryType().toString());
            model.addAttribute("queryScope", query.getQueryScope().toString());
            model.addAttribute("keyword", keyword);
            model.addAttribute("spaceId", spaceId);
            model.addAttribute("creatorEmail", creatorEmail);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }

        return "message-search";
    }

}
