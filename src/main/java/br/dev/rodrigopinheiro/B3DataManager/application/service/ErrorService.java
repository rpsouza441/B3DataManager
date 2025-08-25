
package br.dev.rodrigopinheiro.B3DataManager.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
@Service
public class ErrorService {

    private final ResourceBundle messages;

    public ErrorService() {
        // Locale padrão da aplicação
        Locale currentLocale = Locale.getDefault();
        this.messages = ResourceBundle.getBundle("messages", currentLocale);
    }

    /**
     * Retorna uma mensagem formatada com base em uma chave de mensagens e parâmetros.
     *
     * @param key    Chave da mensagem no arquivo de mensagens.
     * @param params Parâmetros para a formatação da mensagem.
     * @return Mensagem formatada.
     */
    public String getErrorMessage(String key, Object... params) {
        try {
            String message = messages.getString(key);
            return String.format(message, params);
        } catch (Exception e) {
            log.error("Erro ao carregar mensagem de erro com a chave: {}", key, e);
            return "Erro desconhecido.";
        }
    }

    /**
     * Registra o erro nos logs.
     *
     * @param message Mensagem de erro.
     * @param e       Exceção associada ao erro.
     */
    public void logError(String message, Throwable e) {
        log.error(message, e);
    }
}
