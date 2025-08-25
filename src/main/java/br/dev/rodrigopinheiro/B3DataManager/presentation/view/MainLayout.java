package br.dev.rodrigopinheiro.B3DataManager.presentation.view;


import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.presentation.Service.ThemeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {


    List<String> exclusionList = Arrays.asList("/register", "/login");
    private final ResourceBundle messages;
    private SecurityService securityService;

    private H1 viewTitle;

    private final ThemeService themeService;

    public MainLayout(SecurityService securityService, ThemeService themeService) {
        this.themeService = themeService;
        this.securityService = securityService;

        // Aplicar tema
        themeService.applySystemTheme();

        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        this.messages = ResourceBundle.getBundle("messages", currentLocale);

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {

        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("B3 Data");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (ifLoggedinAndExcluddedOrNotLoggedin(entry)) {
                String nomeMenu = entry.path().isEmpty() ?
                        messages.getString("home.menu") :
                        messages.getString(entry.path().substring(1) + ".menu");


                if (entry.icon() != null) {
                    nav.addItem(new SideNavItem(nomeMenu ,
                            entry.path(),
                            new SvgIcon(entry.icon())));
                } else {
                    nav.addItem(new SideNavItem(nomeMenu ,
                            entry.path()));
                }



            }
        });
        if (securityService.isUserLoggedIn()) {
            nav.addItem(createLogoutNavItem());
        }

        return nav;
    }

    private boolean ifLoggedinAndExcluddedOrNotLoggedin(MenuEntry entry) {
        return (securityService.isUserLoggedIn() && !exclusionList.contains(entry.path())) || !SecurityService.isUserLoggedIn();
    }

    private SideNavItem createLogoutNavItem() {
        // Cria o item de navegação de Logout com redirecionamento para uma URL de logout
        SideNavItem logoutItem = new SideNavItem("Logout", "javascript:void(0)", (VaadinIcon.SIGN_OUT_ALT.create()));

        // Adiciona o listener de clique para realizar o logout
        logoutItem.getElement().addEventListener("click", event -> {
            securityService.logout();
        });


        return logoutItem;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

//        Optional<Usuario> maybeUser = authenticatedUser.get();
//        if (maybeUser.isPresent()) {
//            User user = maybeUser.get();
//
//            Avatar avatar = new Avatar(user.getName());
//            StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
//            avatar.setImageResource(resource);
//            avatar.setThemeName("xsmall");
//            avatar.getElement().setAttribute("tabindex", "-1");
//
//            MenuBar userMenu = new MenuBar();
//            userMenu.setThemeName("tertiary-inline contrast");
//
//            MenuItem userName = userMenu.addItem("");
//            Div div = new Div();
//            div.add(avatar);
//            div.add(user.getName());
//            div.add(new Icon("lumo", "dropdown"));
//            div.getElement().getStyle().set("display", "flex");
//            div.getElement().getStyle().set("align-items", "center");
//            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
//            userName.add(div);
//            userName.getSubMenu().addItem("Sign out", e -> {
//                authenticatedUser.logout();
//            });
//
//            layout.add(userMenu);
//        } else {
        Anchor loginLink = new Anchor("login", "Sign in");
        layout.add(loginLink);
//        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        // Obtém o caminho da rota atual sem o "/"
        String route = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
        if (route == null || route.isBlank() || route.isEmpty()) {
            route = "home";
        }

        // Define o título no layout usando a rota como chave
        viewTitle.setText(messages.getString(route + ".title"));

        // Define o título da aba do navegador
        String fullTitle = messages.getString(route + ".title") + " | " + messages.getString("global.project.name");
        UI.getCurrent().getPage().setTitle(fullTitle);
    }


}

