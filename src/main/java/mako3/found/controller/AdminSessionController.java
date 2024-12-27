package mako3.found.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class AdminSessionController {

    @GetMapping("/admin/session")
    public Map<String, Object> checkSession(HttpSession session) {
        return Map.of("maxInactiveInterval", session.getMaxInactiveInterval(),
                "lastAccessedTime", session.getLastAccessedTime(),
                "creationTime", session.getCreationTime(),
                "id", session.getId());
    }

}
