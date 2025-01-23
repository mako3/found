package mako3.found.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import mako3.found.auth.CustomUserDetails;
import mako3.found.entity.ChatSpace;
import mako3.found.json.JsonException;
import mako3.found.service.FileSystemStorageService;
import mako3.found.service.MessageImportService;
import mako3.found.service.SpaceService;

@Controller
public class AdminSpacesController {

    private static Log logger = LogFactory.getLog(AdminSpacesController.class);

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private MessageImportService messageImportService;

    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping("/admin/spaces")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminSpaces(Model model) {
        List<ChatSpace> spaceList = spaceService.listAllFresh();
        model.addAttribute("spaceList", spaceList);
        model.addAttribute("spaceCount", spaceList.size());
        return "admin-spaces";
    }

    @GetMapping("/importJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String importJson(@CurrentSecurityContext SecurityContext context, @RequestParam("spaceId") String spaceId,
            @RequestParam("filenameOfMessagesJson") String filenameOfMessagesJson,
            @RequestParam("filenameOfGroupInfoJson") String filenameOfGroupInfoJson) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        long t1 = System.currentTimeMillis();
        logger.info(String.format("Going to import json for space %s.", spaceId));
        messageImportService.importJson(spaceId, filenameOfMessagesJson, filenameOfGroupInfoJson, user.getUsername());
        long t2 = System.currentTimeMillis();
        logger.info(String.format("Succeeded to import json for space %s in %d msec.", spaceId, t2 - t1));

        return "string";
    }

    @PostMapping("/uploadMessagesJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String uploadMessagesJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spaceId") String spaceId) throws JsonException {

        String fileName = defineFilenamePrefix(spaceId) + "_" + file.getOriginalFilename();
        storageService.store(file, fileName);

        return fileName;
    }

    @PostMapping("/uploadGroupInfoJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String uploadGroupInfoJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spaceId") String spaceId) throws JsonException {

        String fileName = defineFilenamePrefix(spaceId) + "_" + file.getOriginalFilename();
        storageService.store(file, fileName);
        return fileName;
    }

    private String defineFilenamePrefix(String spaceId) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String now = LocalDateTime.now().format(f);
        return String.join("_", spaceId, now);
    }

}
