package br.dev.rodrigopinheiro.B3DataManager.domain.model;


import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Portfolio {
    private Long id;
    private Usuario usuario;
    private Set<AtivoFinanceiroEntity> ativosFinanceiro = new HashSet<>();
    private List<TransacaoEntity> transacoes = new ArrayList<>();
    private BigDecimal saldoTotal;
    private BigDecimal saldoAplicado;
    private BigDecimal lucroVenda;
    private BigDecimal lucroRendimento;

    public Portfolio() {
    }

    public Portfolio(Long id, Usuario usuario, Set<AtivoFinanceiroEntity> ativosFinanceiro, List<TransacaoEntity> transacoes, BigDecimal saldoTotal, BigDecimal saldoAplicado, BigDecimal lucroVenda, BigDecimal lucroRendimento) {
        this.id = id;
        this.usuario = usuario;
        this.ativosFinanceiro = ativosFinanceiro;
        this.transacoes = transacoes;
        this.saldoTotal = saldoTotal;
        this.saldoAplicado = saldoAplicado;
        this.lucroVenda = lucroVenda;
        this.lucroRendimento = lucroRendimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<AtivoFinanceiroEntity> getAtivosFinanceiro() {
        return ativosFinanceiro;
    }

    public void setAtivosFinanceiro(Set<AtivoFinanceiroEntity> ativosFinanceiro) {
        this.ativosFinanceiro = ativosFinanceiro;
    }

    public List<TransacaoEntity> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoEntity> transacoes) {
        this.transacoes = transacoes;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public BigDecimal getSaldoAplicado() {
        return saldoAplicado;
    }

    public void setSaldoAplicado(BigDecimal saldoAplicado) {
        this.saldoAplicado = saldoAplicado;
    }

    public BigDecimal getLucroVenda() {
        return lucroVenda;
    }

    public void setLucroVenda(BigDecimal lucroVenda) {
        this.lucroVenda = lucroVenda;
    }

    public BigDecimal getLucroRendimento() {
        return lucroRendimento;
    }

    public void setLucroRendimento(BigDecimal lucroRendimento) {
        this.lucroRendimento = lucroRendimento;
    }
}


