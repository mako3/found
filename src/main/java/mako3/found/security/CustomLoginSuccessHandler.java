package mako3.found.security;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mako3.found.auth.CustomUserDetails;
import mako3.found.auth.CustomUserDetailsService;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static Log logger = LogFactory.getLog(CustomLoginSuccessHandler.class);

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private CustomUserDetailsService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        userService.updateLastLogin(user.getUsername());

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String targetUrl;
        if (user.getForceChangePassword()) {
            targetUrl = "/user/password/change";
        } else if (savedRequest != null) {
            targetUrl = savedRequest.getRedirectUrl();
        } else {
            targetUrl = "/";
        }

        logger.info(String.format("login succeeded by %s, redirect into %s", user.getUsername(), targetUrl));
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}
