package br.dev.rodrigopinheiro.B3DataManager.presentation.view.register;

import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.UsuarioService;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.UsuarioException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Locale;
import java.util.ResourceBundle;


@Route(value = "register")
@Menu(order = 3, icon = LineAwesomeIconUrl.ID_CARD)
@AnonymousAllowed
public class RegisterView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle, HasUrlParameter<Long> {

    private final UsuarioService usuarioService;
    private final ResourceBundle messages;
    private String title = "";

    @Autowired
    public RegisterView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        // Configuração de i18n
        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        this.messages = ResourceBundle.getBundle("messages", currentLocale);

        // Main layout configuration
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("view-background");


        // Título principal do projeto
        H1 projectTitle = new H1(messages.getString("global.project.name"));
        projectTitle.addClassName("project-title");
        add(projectTitle);

        // Contêiner do título principal
        Div titleContainer = new Div();
        titleContainer.addClassName("centered-container");

        // Título principal
        NativeLabel mainTitle = new NativeLabel(messages.getString("register.title.form"));
        mainTitle.addClassName("main-title");

        // Adicionando o título ao contêiner
        titleContainer.add(mainTitle);

        // Subtítulo "Cadastro"
        NativeLabel subTitle = new NativeLabel(messages.getString("register.title.h1"));
        subTitle.addClassName("sub-title");

        // Contêiner de erro
        Div errorNotification = new Div();
        errorNotification.addClassName("error-notification");

        // Container for the form to limit width
        Div formContainer = new Div();
        formContainer.addClassName("form-container");

        // Form fields
        TextField usernameField = createTextField(messages.getString("register.username"));
        EmailField emailField = createEmailField(messages.getString("register.email"));
        PasswordField passwordField = createPasswordField(messages.getString("register.password"));
        PasswordField confirmPasswordField = createPasswordField(messages.getString("register.confirmPassword"));

        // Habilitando navegação com a tecla Enter entre os campos
        enableEnterKeyNavigation(usernameField, emailField, passwordField, confirmPasswordField);

        // Password strength indicator
        ProgressBar strengthIndicator = new ProgressBar();
        strengthIndicator.setWidth("100%");
        strengthIndicator.setMax(4);

        NativeLabel strengthLabel = new NativeLabel(messages.getString("register.passwordStrength"));
        strengthLabel.addClassName("strength-label");

        passwordField.addValueChangeListener(event -> {
            String password = event.getValue();
            int strength = calculatePasswordStrength(password);
            strengthIndicator.setValue(strength);

            String label = switch (strength) {
                case 1 -> messages.getString("strength.veryWeak");
                case 2 -> messages.getString("strength.weak");
                case 3 -> messages.getString("strength.good");
                case 4 -> messages.getString("strength.strong");
                default -> messages.getString("strength.veryWeak");
            };
            strengthLabel.setText(messages.getString("register.passwordStrength") + ": " + label);
        });

        Div strengthContainer = new Div(strengthLabel, strengthIndicator);
        strengthContainer.addClassName("strength-container");

        // Register button
        Button registerButton = new Button(messages.getString("register.button"));
        registerButton.setEnabled(false);
        registerButton.addClassName("register-button");


        confirmPasswordField.addValueChangeListener(event -> {
            confirmPasswordField.setInvalid(!passwordField.getValue().equals(confirmPasswordField.getValue()));
            registerButton.setEnabled(isFormValid(usernameField, emailField, passwordField, confirmPasswordField));
        });

        registerButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();

            try {
                usuarioService.registerUser(username, email, password, currentLocale);
                Notification.show(messages.getString("register.success"), 3000, Notification.Position.TOP_CENTER);
                clearFields(usernameField, emailField, passwordField, confirmPasswordField);
                strengthIndicator.setValue(0);
                strengthLabel.setText(messages.getString("register.passwordStrength"));
                errorNotification.setText(""); // Clear previous error
                errorNotification.getStyle().set("display", "none"); // Hide notification

                // Redireciona para o login após o cadastro bem-sucedido
                getUI().ifPresent(ui -> ui.navigate("/login"));

            } catch (UsuarioException e) {
                showInlineNotification(e.getMessage(), errorNotification);
            }
        });

        // Link para voltar ao login
        Div loginRedirect = new Div();
        loginRedirect.addClassName("login-redirect");
        Anchor loginLink = new Anchor("/login", messages.getString("register.loginLink"));
        loginLink.addClassName("register-login-link");
        loginRedirect.add(loginLink);

        // Adicionando os títulos diretamente ao contêiner externo
        formContainer.add(mainTitle, subTitle, errorNotification);

// Criando o contêiner interno para centralizar os inputs
        Div innerContainer = new Div();
        innerContainer.addClassName("inner-form-container");

// Adicionando os campos ao contêiner interno
        innerContainer.add(usernameField, emailField, passwordField, confirmPasswordField, strengthContainer, registerButton, loginRedirect);

// Adicionando o contêiner interno ao contêiner externo
        formContainer.add(innerContainer);

// Adicionando o contêiner externo ao layout principal
        add(formContainer);
    }

    private TextField createTextField(String label) {
        TextField textField = new TextField(label);
        textField.setRequired(true);
        textField.setWidth("100%");
        textField.setErrorMessage(label + " " + messages.getString("field.required"));
        return textField;
    }

    private EmailField createEmailField(String label) {
        EmailField emailField = new EmailField(label);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidth("100%");
        emailField.setErrorMessage(messages.getString("register.invalidEmail"));
        return emailField;
    }

    private PasswordField createPasswordField(String label) {
        PasswordField passwordField = new PasswordField(label);
        passwordField.setRequired(true);
        passwordField.setWidth("100%");
        passwordField.setErrorMessage(label + " " + messages.getString("field.required"));
        return passwordField;
    }

    private boolean isFormValid(TextField usernameField, EmailField emailField, PasswordField passwordField, PasswordField confirmPasswordField) {
        return !usernameField.isEmpty() && !emailField.isEmpty() && !emailField.isInvalid() && !passwordField.isEmpty() && passwordField.getValue().equals(confirmPasswordField.getValue());
    }

    private void clearFields(TextField usernameField, EmailField emailField, PasswordField passwordField, PasswordField confirmPasswordField) {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[@#$%^&+=!].*")) strength++;

        return strength;
    }

    private void showInlineNotification(String errorReason, Div notificationContainer) {
        // Título do erro
        Div title = new Div();
        title.setText(messages.getString("register.error.title"));
        title.addClassName("inline-notification-title");

        // Mensagem de erro
        Div message = new Div();
        message.setText(errorReason);
        message.addClassName("inline-notification-message");

        // Limpa o conteúdo anterior e adiciona o novo
        notificationContainer.removeAll();
        notificationContainer.add(title, message);
        notificationContainer.addClassName("inline-notification-container");
    }

    private void enableEnterKeyNavigation(Component... fields) {
        for (int i = 0; i < fields.length; i++) {
            int nextIndex = i + 1;
            fields[i].getElement().addEventListener("keydown", e -> {
                if ("Enter".equals(e.getEventData().getString("event.key")) && nextIndex < fields.length) {
                    fields[nextIndex].getElement().callJsFunction("focus");
                }
            }).addEventData("event.key");
        }
    }


    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityService.isUserLoggedIn()) {
            // Redireciona para a página inicial se o usuário já estiver logado
            event.forwardTo("");
        }

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
