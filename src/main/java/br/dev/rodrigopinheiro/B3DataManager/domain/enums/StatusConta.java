package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

public enum StatusConta {
    ATIVA("Conta ativa"),
    INATIVA("Conta inativa"),
    SUSPENSA("Conta suspensa"),
    BLOQUEADA("Conta bloqueada");

    private final String descricao;

    StatusConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}