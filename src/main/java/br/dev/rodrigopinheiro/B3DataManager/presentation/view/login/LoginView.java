package br.dev.rodrigopinheiro.B3DataManager.presentation.view.login;

import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.UsuarioService;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Locale;
import java.util.ResourceBundle;

@Route(value = "login")
@Menu(order = 3, icon = LineAwesomeIconUrl.USER_CIRCLE)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle, HasUrlParameter<Long> {


    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final LoginForm loginForm = new LoginForm();
    private final ResourceBundle messages;
    private String title = "";

    public LoginView() {

        Locale currentLocale = getUI().map(ui -> ui.getSession().getLocale()).orElse(Locale.getDefault());
        messages = ResourceBundle.getBundle("messages", currentLocale);

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Título principal do projeto
        H1 projectTitle = new H1(messages.getString("global.project.name"));
        projectTitle.addClassName("project-title");
        add(projectTitle);

        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.setAction("login");

        loginForm.setI18n(createLoginI18n());

        // Container para o formulário
        Div formContainer = new Div();
        formContainer.addClassName("form-container");

        H1 title = new H1(messages.getString("login.title.h1"));
        title.addClassName("form-title");

        Div linkContainer = new Div();
        linkContainer.addClassName("link-container");

        Anchor registerLink = new Anchor("register", messages.getString("login.registerLink"));
        registerLink.addClassName("register-login-link");

        linkContainer.add(registerLink);

        formContainer.add(title, loginForm, linkContainer);
        add(formContainer);
    }

    private LoginI18n createLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setUsername(messages.getString("login.username"));
        i18n.getForm().setPassword(messages.getString("login.password"));
        i18n.getForm().setTitle(messages.getString("login.title.form"));
        i18n.getForm().setSubmit(messages.getString("login.submit"));
        i18n.getErrorMessage().setTitle(messages.getString("login.error.title"));
        i18n.getErrorMessage().setMessage(messages.getString("login.error.message"));
        return i18n;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityService.isUserLoggedIn()) {
            event.forwardTo("");
        } else if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
            logger.warn("Login attempt failed.");
        }
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {

        title = messages.getString("login.title") + " | " + messages.getString("global.project.name");
        if (parameter != null) {
            title = title + parameter;
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }



}
