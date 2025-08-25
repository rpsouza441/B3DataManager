package br.dev.rodrigopinheiro.B3DataManager.application.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";

    public CustomUserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        log.debug("Recuperando usuário autenticado. SecurityContext: {}", context);

        if (authentication == null) {
            log.warn("Authentication está nulo.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.debug("Principal encontrado: {}", principal);

        if (principal instanceof CustomUserDetails customUserDetails) {
            log.info("Usuário autenticado recuperado com sucesso: {}", customUserDetails.getUsername());
            return customUserDetails;
        }

        log.warn("Principal não é uma instância de CustomUserDetails. Tipo: {}", principal != null ? principal.getClass() : "null");
        return null;
    }

    public void logout() {
        log.info("Usuário solicitou logout.");
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
        log.info("Logout realizado com sucesso.");
    }

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Authentication está nulo ao tentar recuperar o ID do usuário.");
            return null;
        }

        log.debug("Authentication encontrado: {}", authentication);

        if (authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();
            log.debug("Principal encontrado: {}", principal);

            if (principal instanceof CustomUserDetails customUserDetails) {
                log.info("ID do usuário autenticado recuperado com sucesso: {}", customUserDetails.getId());
                return customUserDetails.getId();
            } else {
                log.warn("Principal não é do tipo CustomUserDetails. Tipo: {}", principal != null ? principal.getClass() : "null");
            }
        } else {
            log.warn("Usuário não está autenticado ou é um token anônimo.");
        }

        return null;
    }

    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Authentication está nulo ao verificar se o usuário está logado.");
            return false;
        }

        boolean isLoggedIn = authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);

        return isLoggedIn;
    }
}
