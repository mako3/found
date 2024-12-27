package mako3.found.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import mako3.found.auth.CustomUserDetails;
import mako3.found.entity.ChatSpace;
import mako3.found.entity.NewSpace;
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

    @GetMapping("/admin/importJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> importJson(@CurrentSecurityContext SecurityContext context,
            @RequestParam("spaceId") String spaceId,
            @RequestParam("filenameOfMessagesJson") String filenameOfMessagesJson,
            @RequestParam("filenameOfGroupInfoJson") String filenameOfGroupInfoJson) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();

        logger.info(String.format("Going to import json for space %s.", spaceId));

        try {
            long t1 = System.currentTimeMillis();
            messageImportService.importJson(spaceId, filenameOfMessagesJson, filenameOfGroupInfoJson,
                    user.getUsername());
            long t2 = System.currentTimeMillis();
            logger.info(String.format("Succeeded to import json for space %s in %d msec.", spaceId, t2 - t1));
        } catch (JsonException e) {
            logger.error(String.format("failed to import json for space %s", spaceId), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error(String.format("failed to import json for space %s", spaceId), e);
            return ResponseEntity.internalServerError().body("Jsonのインポートに失敗しました。");
        }

        return ResponseEntity.ok(String.format("Succeeded to import json for space %s", spaceId));
    }

    @PostMapping("/admin/uploadMessagesJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> uploadMessagesJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spaceId") String spaceId) throws JsonException {

        if (!"messages.json".equals(file.getOriginalFilename())) {
            logger.error(String.format("%s is not expcted filename.", file.getOriginalFilename()));
            return ResponseEntity.badRequest().body("Invalid filename.");
        }

        String fileName = defineFilenamePrefix(spaceId) + "_" + file.getOriginalFilename();
        storageService.store(file, fileName);

        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/admin/uploadGroupInfoJson")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> uploadGroupInfoJson(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spaceId") String spaceId) throws JsonException {

        if (!"group_info.json".equals(file.getOriginalFilename())) {
            logger.error(String.format("%s is not expcted filename.", file.getOriginalFilename()));
            return ResponseEntity.badRequest().body("Invalid filename.");
        }

        String fileName = defineFilenamePrefix(spaceId) + "_" + file.getOriginalFilename();
        storageService.store(file, fileName);
        return ResponseEntity.ok(fileName);
    }

    private String defineFilenamePrefix(String spaceId) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String now = LocalDateTime.now().format(f);
        return String.join("_", spaceId, now);
    }

    @PostMapping("/admin/addSpace")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addSpace(@CurrentSecurityContext SecurityContext context,
            @Validated NewSpace newSpace,
            BindingResult result, Model model) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input.");
        }

        try {
            spaceService.addSpace(newSpace);
            logger.info(String.format("succeeded to add space %s by %s", newSpace.getSpaceId(), user.getUsername()));
        } catch (DuplicateKeyException e) {
            logger.error(String.format("failed to add space %s for duplicate key", newSpace.getSpaceId()), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already exists.");
        }
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/admin/deleteSpace")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSpace(@CurrentSecurityContext SecurityContext context,
            @RequestParam("spaceId") String spaceId) {
        CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
        spaceService.deleteSpace(spaceId);
        logger.info(String.format("succeeded to delete space %s by %s", spaceId, user.getUsername()));
        return ResponseEntity.ok("success");
    }

}
