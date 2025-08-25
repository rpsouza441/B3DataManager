package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando um email já está registrado.
 */
public class EmailAlreadyExistsException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção.
     *
     * @param email         O email que já está registrado.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public EmailAlreadyExistsException(String email, MessageSource messageSource) {
        super("email.already_exists", messageSource, email);
    }
}
