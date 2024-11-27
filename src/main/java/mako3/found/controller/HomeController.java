package mako3.found.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mako3.found.auth.CustomUserDetails;
import mako3.found.entity.ChatSpace;
import mako3.found.service.SpaceService;

@Controller
public class HomeController {

	@Autowired
	private SpaceService spaceService;

	@GetMapping(value = "/")
	public String home(@CurrentSecurityContext SecurityContext context, Model model) {
		CustomUserDetails user = (CustomUserDetails) context.getAuthentication().getPrincipal();
		List<ChatSpace> list = spaceService.findByMember(user.getEmailForMessageIdentity());
		model.addAttribute("userList", Arrays.asList(user));
		model.addAttribute("spaceList", list);

		return "home";
	}

}
