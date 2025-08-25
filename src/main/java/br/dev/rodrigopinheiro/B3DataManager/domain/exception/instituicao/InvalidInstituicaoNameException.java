package br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando o nome de uma instituição é inválido.
 */
public class InvalidInstituicaoNameException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção.
     *
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InvalidInstituicaoNameException(MessageSource messageSource) {
        super("instituicao.invalid_name", messageSource);
    }
}
