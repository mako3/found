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

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String targetUrl = savedRequest != null ? savedRequest.getRedirectUrl() : "/";

        String userName = authentication.getName();
        userService.updateLastLogin(userName);

        logger.info(String.format("login succeeded by %s", userName));
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

}
