package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RendaVariavel {
    private Long id;
    private LocalDate dataCompra;
    private BigDecimal precoUnitario;
    private double quantidade;
    private BigDecimal total;
    private AtivoFinanceiroEntity ativoFinanceiro;
    private String tipoRendaVariavel;

    public RendaVariavel() {
    }

    public RendaVariavel(Long id, LocalDate dataCompra, BigDecimal precoUnitario, double quantidade, BigDecimal total, AtivoFinanceiroEntity ativoFinanceiro, String tipoRendaVariavel) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.total = total;
        this.ativoFinanceiro = ativoFinanceiro;
        this.tipoRendaVariavel = tipoRendaVariavel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public AtivoFinanceiroEntity getAtivoFinanceiro() {
        return ativoFinanceiro;
    }

    public void setAtivoFinanceiro(AtivoFinanceiroEntity ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }

    public String getTipoRendaVariavel() {
        return tipoRendaVariavel;
    }

    public void setTipoRendaVariavel(String tipoRendaVariavel) {
        this.tipoRendaVariavel = tipoRendaVariavel;
    }
}
