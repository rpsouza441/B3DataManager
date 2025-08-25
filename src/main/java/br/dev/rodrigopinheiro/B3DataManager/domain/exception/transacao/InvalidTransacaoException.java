package br.dev.rodrigopinheiro.B3DataManager.domain.exception.transacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção personalizada para indicar que uma transação inválida foi detectada.
 */
public class InvalidTransacaoException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InvalidTransacaoException(MessageSource messageSource) {
        super("transacao.null", messageSource);
    }
}
