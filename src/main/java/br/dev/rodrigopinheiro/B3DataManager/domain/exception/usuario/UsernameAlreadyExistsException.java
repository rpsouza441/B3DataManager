package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando um nome de usuário já está registrado.
 */
public class UsernameAlreadyExistsException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção.
     *
     * @param username      O nome de usuário que já está registrado.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public UsernameAlreadyExistsException(String username, MessageSource messageSource) {
        super("username.already_exists", messageSource, username);
    }
}
