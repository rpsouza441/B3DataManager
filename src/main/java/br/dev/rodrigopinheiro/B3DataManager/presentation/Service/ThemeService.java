package br.dev.rodrigopinheiro.B3DataManager.presentation.Service;


import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    private static final String DARK_THEME = "dark";
    private static final String LIGHT_THEME = "light";

    public void applySystemTheme() {
        String js = """
            (function() {
                var applyTheme = function(isDark) {
                    document.documentElement.setAttribute('theme', isDark ? 'dark' : 'light');
                };

                // Detectar o tema inicial
                var mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
                applyTheme(mediaQuery.matches);

                // Monitorar mudanças no tema do sistema
                mediaQuery.addEventListener('change', function(event) {
                    applyTheme(event.matches);
                    $0.$server.themeChanged(event.matches);
                });
            })();
        """;

        // Execute o JavaScript no cliente
        UI.getCurrent().getElement().executeJs(js);
    }

    public void themeChanged(Boolean isDark) {
        // Sincronizar o tema no servidor
        UI.getCurrent().getElement().setAttribute("theme", isDark ? DARK_THEME : LIGHT_THEME);
    }

}