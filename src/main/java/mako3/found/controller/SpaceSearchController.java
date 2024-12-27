package mako3.found.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mako3.found.entity.ChatSpace;
import mako3.found.service.SpaceService;

@Controller
public class SpaceSearchController {

    @Autowired
    private SpaceService service;

    @GetMapping(value = "/spaces")
    public String spaces(
            @CurrentSecurityContext SecurityContext context,
            @RequestParam(name = "displayName", required = false) String displayName,
            Model model) {

        if (displayName != null) {
            long t1 = System.currentTimeMillis();
            List<ChatSpace> list = service.findByName(displayName);
            model.addAttribute("keyword", displayName);
            model.addAttribute("spaceList", list);
            long t2 = System.currentTimeMillis();
            model.addAttribute("queryTime", t2 - t1);

        }

        return "space-search";
    }

}
