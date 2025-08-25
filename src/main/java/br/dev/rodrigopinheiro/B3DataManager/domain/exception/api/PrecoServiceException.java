package br.dev.rodrigopinheiro.B3DataManager.domain.exception.api;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção personalizada para erros no serviço de preços.
 */
public class PrecoServiceException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageSource O MessageSource para localizar mensagens.
     * @param cause         A causa do erro para ser incluída na mensagem.
     */
    public PrecoServiceException(MessageSource messageSource, String cause) {
        super("preco.service.error", messageSource, cause);
    }
}
