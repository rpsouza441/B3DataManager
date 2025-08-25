package br.dev.rodrigopinheiro.B3DataManager.domain.exception.api;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção personalizada para indicar uma lista inválida de tickers.
 */
public class InvalidTickerListException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InvalidTickerListException(MessageSource messageSource) {
        super("ticker.list.invalid", messageSource);
    }
}
