package br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando uma operação não é encontrada.
 */
public class OperacaoNotFoundException extends B3DataManagerException {

    private final Long operacaoId;

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param operacaoId    ID da operação não encontrada.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public OperacaoNotFoundException(Long operacaoId, MessageSource messageSource) {
        super("operacao.not_found", messageSource, operacaoId);
        this.operacaoId = operacaoId;
    }

    public Long getOperacaoId() {
        return operacaoId;
    }
}
