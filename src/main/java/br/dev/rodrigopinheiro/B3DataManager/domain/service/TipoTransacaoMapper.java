package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;

public interface TipoTransacaoMapper {
    /**
     * Converte uma descrição textual e um sinal em um TipoTransacao correspondente.
     *
     * @param sinal      o sinal da transação ("ENTRADA" ou "SAIDA")
     * @param descricao  a descrição da movimentação
     * @return           o TipoTransacao correspondente, ou OUTRA se não houver correspondência
     */
    TipoTransacao mapear(String sinal, String descricao);
}
