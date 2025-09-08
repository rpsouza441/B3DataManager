package br.dev.rodrigopinheiro.B3DataManager.domain.model;


import java.util.ArrayList;
import java.util.List;

public class AtivoFinanceiro {
    private Long id;

    private String nome;

    private Portfolio portfolio;

    private List<Transacao> transacoes;

    private List<RendaVariavel> rendaVariaveis = new ArrayList<>();

    private List<RendaFixa> rendaFixas;

    private Boolean deletado = false;
}
