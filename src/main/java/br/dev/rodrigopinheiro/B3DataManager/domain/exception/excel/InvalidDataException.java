package br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel;

import org.springframework.context.MessageSource;

public class InvalidDataException extends RuntimeException {

    private final String messageKey;

    /**
     * Construtor que utiliza o MessageSource para localizar a mensagem.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param messageArgs   Argumentos opcionais para formatação da mensagem.
     */
    public InvalidDataException(String messageKey, MessageSource messageSource, Object... messageArgs) {
        super(getLocalizedMessage(messageKey, messageSource, messageArgs));
        this.messageKey = messageKey;
    }

    /**
     * Construtor para casos com mensagens personalizadas e causas.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param cause         Exceção original que causou o erro.
     * @param messageArgs   Argumentos opcionais para formatação da mensagem.
     */
    public InvalidDataException(String messageKey, MessageSource messageSource, Throwable cause, Object... messageArgs) {
        super(getLocalizedMessage(messageKey, messageSource, messageArgs), cause);
        this.messageKey = messageKey;
    }

    /**
     * Obtém a mensagem localizada com base no MessageSource.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param messageArgs   Argumentos opcionais para formatação da mensagem.
     * @return Mensagem localizada e formatada.
     */
    private static String getLocalizedMessage(String messageKey, MessageSource messageSource, Object... messageArgs) {
        return messageSource.getMessage(messageKey, messageArgs, null); // Locale será gerenciado pelo Spring
    }

    /**
     * Obtém a chave da mensagem associada a esta exceção.
     *
     * @return A chave da mensagem.
     */
    public String getMessageKey() {
        return messageKey;
    }
}
