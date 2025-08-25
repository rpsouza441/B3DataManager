package br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory;

import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import lombok.extern.slf4j.Slf4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
public class MessageUtils {

    private final ResourceBundle messages;

    public MessageUtils(ResourceBundle messages) {
        this.messages = messages;
    }

    /**
     * Obtém a string correspondente à chave fornecida.
     *
     * @param key A chave para buscar a mensagem.
     * @return A mensagem correspondente ou uma mensagem padrão caso a chave não seja encontrada.
     */
    public String getString(String key) {
        try {
            return messages.getString(key);
        } catch (MissingResourceException e) {
            // Log do erro para ajudar na depuração
            // Loga o erro
            log.error("Chave ausente no arquivo de mensagens: {}", key, e);

            // Notifica o usuário
            ToastNotification.showError("Erro ao carregar texto: " + key);

            // Retorna uma mensagem padrão ou a própria chave para evitar falhas
            return "[" + key + "]";
        }
    }

    /**
     * Retorna uma mensagem formatada com parâmetros.
     *
     * @param key    Chave da mensagem.
     * @param params Parâmetros para formatação.
     * @return Texto da mensagem formatada.
     */
    public String getFormattedString(String key, Object... params) {
        return String.format(messages.getString(key), params);
    }
}
