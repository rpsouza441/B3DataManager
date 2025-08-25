package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando um nome de usuário é inválido.
 */
public class InvalidUsernameException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InvalidUsernameException(String messageKey, MessageSource messageSource) {
        super(messageKey, messageSource);
    }
}
