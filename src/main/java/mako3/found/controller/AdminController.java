package mako3.found.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import mako3.found.auth.CustomUserDetailsService;
import mako3.found.entity.ChatSpace;
import mako3.found.json.JsonException;
import mako3.found.service.FileSystemStorageService;
import mako3.found.service.MessageImportService;
import mako3.found.service.SpaceService;

@Controller
public class AdminController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private MessageImportService messageImportService;

    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(Model model) {
        List<ChatSpace> spaceList = spaceService.findAll();
        List<CustomUserDetails> userList = userService.loadAllUsers();
        model.addAttribute("userList", userList);
        model.addAttribute("spaceList", spaceList);
        return "admin";
    }

    @GetMapping("/importJson")
    @ResponseBody
    public String importJson(@CurrentSecurityContext SecurityContext context, @RequestParam("spaceId") String spaceId,
            @RequestParam("filenameOfMessagesJson") String filenameOfMessagesJson,
            @RequestParam("filenameOfGroupInfoJson") String filenameOfGroupInfoJson) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        messageImportService.importJson(spaceId, filenameOfMessagesJson, filenameOfGroupInfoJson, user.getUsername());

        return "string";
    }

    @PostMapping("/uploadMessagesJson")
    @ResponseBody
    public String uploadMessagesJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spaceId") String spaceId) throws JsonException {

        String fileName = defineFilenamePrefix(spaceId) + "_" + file.getOriginalFilename();
        storageService.store(file, fileName);

        return fileName;
    }

    @PostMapping("/uploadGroupInfoJson")
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
