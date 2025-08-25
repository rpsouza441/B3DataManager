package br.dev.rodrigopinheiro.B3DataManager.domain.enums;


public enum TipoTransacao {
    ENTRADA,
    SAIDA,
    TAXA,
    VENDA,           // Representa o lucro da venda do ativo
    LUCRO_RENDIMENTO,
    LUCRO_DIVIDENDO,
    LUCRO_JUROS,
    LUCRO_OUTRA,
    TRANSFERENCIA,   // Ativos migrados entre instituições
    OUTRA;


}
