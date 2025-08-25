package br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando um filtro inválido é fornecido.
 */
public class InvalidFilterException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InvalidFilterException(String messageKey, MessageSource messageSource) {
        super(messageKey, messageSource);
    }
}
