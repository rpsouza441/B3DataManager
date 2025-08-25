package br.dev.rodrigopinheiro.B3DataManager.presentation.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class CustomI18NProvider implements I18NProvider {

    private static final String BUNDLE_NAME = "messages"; // Nome do arquivo .properties
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            new Locale("en"), // Inglês
            new Locale("pt", "BR") // Português do Brasil
    );

    @Override
    public List<Locale> getProvidedLocales() {
        return SUPPORTED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);

        if (bundle.containsKey(key)) {
            return String.format(bundle.getString(key), params);
        }

        // Retorna a chave se a tradução não for encontrada
        return key;
    }

    /**
     * Recupera a mensagem com base na chave e no locale atual da sessão Vaadin.
     *
     * @param key Chave da mensagem.
     * @return Mensagem traduzida.
     */
    public static String getMessage(String key, Object... params) {
        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);

        if (bundle.containsKey(key)) {
            return String.format(bundle.getString(key), params);
        }

        // Retorna a chave se a tradução não for encontrada
        return key;
    }
}
