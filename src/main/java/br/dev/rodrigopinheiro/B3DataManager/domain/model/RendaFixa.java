package br.dev.rodrigopinheiro.B3DataManager.domain.model;



import java.math.BigDecimal;
import java.time.LocalDate;

public class RendaFixa {
    
    private Long id;
    private LocalDate dataCompra;
    private BigDecimal precoUnitario;
    private double quantidade;
    private BigDecimal total;
    private AtivoFinanceiro ativoFinanceiro;
    private String tipoRendaFixa;

    public RendaFixa() {
    }

    public RendaFixa(Long id, LocalDate dataCompra, BigDecimal precoUnitario, double quantidade, BigDecimal total, AtivoFinanceiro ativoFinanceiro, String tipoRendaFixa) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.total = total;
        this.ativoFinanceiro = ativoFinanceiro;
        this.tipoRendaFixa = tipoRendaFixa;
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

    public AtivoFinanceiro getAtivoFinanceiro() {
        return ativoFinanceiro;
    }

    public void setAtivoFinanceiro(AtivoFinanceiro ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }

    public String getTipoRendaFixa() {
        return tipoRendaFixa;
    }

    public void setTipoRendaFixa(String tipoRendaFixa) {
        this.tipoRendaFixa = tipoRendaFixa;
    }
}
