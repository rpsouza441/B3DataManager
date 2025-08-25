package br.dev.rodrigopinheiro.B3DataManager.domain.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Exceção base para todas as exceções da aplicação.
 */
public abstract class B3DataManagerException extends RuntimeException {

    private final String messageKey;

    /**
     * Construtor que utiliza o MessageSource para resolver mensagens.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param args          Argumentos opcionais para formatação da mensagem.
     */
    public B3DataManagerException(String messageKey, MessageSource messageSource, Object... args) {
        super(getLocalizedMessage(messageKey, messageSource, args));
        this.messageKey = messageKey;
    }

    /**
     * Obtém a mensagem localizada com base no MessageSource e Locale atual.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param args          Argumentos opcionais para formatação da mensagem.
     * @return Mensagem localizada e formatada.
     */
    private static String getLocalizedMessage(String messageKey, MessageSource messageSource, Object... args) {
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }

    /**
     * Obtém a chave da mensagem associada à exceção.
     *
     * @return A chave da mensagem.
     */
    public String getMessageKey() {
        return messageKey;
    }
}
