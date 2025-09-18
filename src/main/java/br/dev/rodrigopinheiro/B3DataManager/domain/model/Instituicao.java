package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.util.List;

public class Instituicao {
    private Long id;
    private String nome;
    private List<Usuario> usuarios;
    private List<Transacao> transacoes;

    public Instituicao() {
    }

    public Instituicao(Long id, String nome, List<Usuario> usuarios, List<Transacao> transacoes) {
        this.id = id;
        this.nome = nome;
        this.usuarios = usuarios;
        this.transacoes = transacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
