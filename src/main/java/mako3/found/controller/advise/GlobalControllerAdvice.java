package mako3.found.controller.advise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import mako3.found.service.MonitoringSnippetService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private MonitoringSnippetService snippetService;

    @ModelAttribute("monitoringsnippet")
    public String snippet() {
        return snippetService.getSnippet();
    }
}
