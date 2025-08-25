package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;

/**
 * Interface para resolver o tipo de movimentação de uma transação com base em uma operação.
 *
 * Essa abstração facilita o desacoplamento e permite a criação de implementações alternativas ou mocks
 * para testes unitários.
 */
public interface TipoMovimentacaoMapper {

    /**
     * Determina o tipo de movimentação de uma transação com base na operação.
     *
     * @param operacao A operação contendo os dados necessários.
     * @return Uma String representando o tipo de movimentação (conforme o enum TipoMovimentacao).
     */
    String determinarTipoMovimentacao(Operacao operacao);
}
