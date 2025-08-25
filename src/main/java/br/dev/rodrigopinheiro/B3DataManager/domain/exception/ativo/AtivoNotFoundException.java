package br.dev.rodrigopinheiro.B3DataManager.domain.exception.ativo;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando um ativo financeiro não é encontrado.
 */
public class AtivoNotFoundException extends B3DataManagerException {

    private final String ticker;

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param ticker        O identificador (ticker) do ativo financeiro não encontrado.
     * @param messageSource O MessageSource para localização de mensagens.
     */
    public AtivoNotFoundException(String ticker, MessageSource messageSource) {
        super("ativo.not_found", messageSource, ticker);
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }
}
