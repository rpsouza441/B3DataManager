package br.dev.rodrigopinheiro.B3DataManager.presentation.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Locale;
import java.util.ResourceBundle;

@Route(value = "")
@PermitAll
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID, title = "Home")
public class DashboardView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Long> {

    private final ResourceBundle messages;
    private String title = "";

    public DashboardView(HttpServletRequest request, HttpServletResponse response) {

        // Mensagem de boas-vindas
        NativeLabel welcomeLabel = new NativeLabel("Bem-vindo ao Dashboard! Você está autenticado.");
        welcomeLabel.getStyle().set("font-size", "20px").set("margin-bottom", "20px");
        // Configuração de i18n
        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        this.messages = ResourceBundle.getBundle("messages", currentLocale);

        // Botão de Logout
        Button logoutButton = new Button("Logout", event -> {
            // Executa o logout
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            // Redireciona para a página de login
            getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
        });

        logoutButton.getStyle().set("margin-top", "20px");

        // Layout
        setAlignItems(Alignment.CENTER);
        add(welcomeLabel, logoutButton);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {

        title = messages.getString("register.title") + " | " + messages.getString("global.project.name");
        if (parameter != null) {
            title = title + parameter;
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }

}
