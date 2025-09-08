package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;

import java.util.List;

public class Instituicao {
    private Long id;
    private String nome;
    private List<Usuario> usuarios;
    private List<TransacaoEntity> transacoes;

    public Instituicao() {
    }

    public Instituicao(Long id, String nome, List<Usuario> usuarios, List<TransacaoEntity> transacoes) {
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

    public List<TransacaoEntity> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoEntity> transacoes) {
        this.transacoes = transacoes;
    }
}
