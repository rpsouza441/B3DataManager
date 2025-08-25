package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum TipoAtivoFinanceiroFixa {
    TITULO_PUBLICO("Título Público", "Títulos emitidos pelo governo para captar recursos no mercado financeiro."),
    CDB("Certificado de Depósito Bancário", "Título emitido por bancos com taxa pré-determinada de juros."),
    LETRA_FINANCEIRA("Letra Financeira", "Documento que promete pagar um valor futuro determinado em uma data específica."),
    LCI("LCI - Letra de Crédito Imobiliário", "Título emitido por instituição financeira com recursos do crédito imobiliário."),
    LCA("LCA - Letra de Crédito do Agronegócio", "Título semelhante à LCI, mas com recursos do crédito rural ou agrícola."),
    DEBENTURE("Debênture", "Título de dívida emitido por uma corporação para captar fundos de investidores. Oferece pagamentos de juros periódicos e retorno do principal ao vencimento."),
    ETF("Fundo de Índice", "Fundo que busca replicar o desempenho de um índice de mercado, permitindo diversificação com a compra de uma única cota."),
    DESCONHECIDO("Tipo Desconhecido", "Tipo de ativo não identificado ou não reconhecido.");

    private final String descricaoRapida;
    private final String descricaoDetalhada;

    TipoAtivoFinanceiroFixa(String descricaoRapida, String descricaoDetalhada) {
        this.descricaoRapida = descricaoRapida;
        this.descricaoDetalhada = descricaoDetalhada;
    }



}