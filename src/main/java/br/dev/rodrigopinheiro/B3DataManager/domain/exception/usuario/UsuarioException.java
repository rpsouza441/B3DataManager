package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Exceção personalizada para erros genéricos relacionados ao usuário.
 */
public class UsuarioException extends RuntimeException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param args          Argumentos opcionais para formatação da mensagem.
     */
    public UsuarioException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale()));
    }
}
