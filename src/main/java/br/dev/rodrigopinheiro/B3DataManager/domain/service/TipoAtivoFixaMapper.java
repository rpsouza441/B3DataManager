package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;

public interface TipoAtivoFixaMapper {
    /**
     * Mapeia a string do produto para o enum correspondente de TipoAtivoFinanceiroFixa.
     *
     * @param produto a string que contém os dados do produto.
     * @return o enum correspondente ou DESCONHECIDO se não houver correspondência.
     */
    TipoAtivoFinanceiroFixa mapear(String produto);
}
