package br.dev.rodrigopinheiro.B3DataManager.domain.exception.transacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção personalizada para indicar que uma transação não foi encontrada.
 */
public class TransacaoNotFoundException extends B3DataManagerException {

    private final Long transacaoId;

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param transacaoId   ID da transação não encontrada.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public TransacaoNotFoundException(Long transacaoId, MessageSource messageSource) {
        super("transacao.not_found", messageSource, transacaoId);
        this.transacaoId = transacaoId;
    }

    public Long getTransacaoId() {
        return transacaoId;
    }
}
