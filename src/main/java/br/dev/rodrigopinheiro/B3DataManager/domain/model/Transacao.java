package br.dev.rodrigopinheiro.B3DataManager.domain.model;


import java.math.BigDecimal;
import java.time.LocalDate;

public class Transacao {
    private Long id;

    private LocalDate data;

    private String entradaSaida; // Entrada ou Saída

    private double quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal valorTotal;

    private BigDecimal precoMedio;

    private String tipoTransacao;

    private String tipoMovimentacao;

    private Boolean deletado = false;

    private AtivoFinanceiro ativoFinanceiro;

    private Instituicao instituicao;

    private Darf darf;

    private Operacao operacao;

    private Portfolio portfolio;

    public Transacao() {
    }

    public Transacao(Long id, LocalDate data, String entradaSaida, double quantidade, BigDecimal precoUnitario, BigDecimal valorTotal, BigDecimal precoMedio, String tipoTransacao, String tipoMovimentacao, Boolean deletado, AtivoFinanceiro ativoFinanceiro, Instituicao instituicao, Darf darf, Operacao operacao, Portfolio portfolio) {
        this.id = id;
        this.data = data;
        this.entradaSaida = entradaSaida;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = valorTotal;
        this.precoMedio = precoMedio;
        this.tipoTransacao = tipoTransacao;
        this.tipoMovimentacao = tipoMovimentacao;
        this.deletado = deletado;
        this.ativoFinanceiro = ativoFinanceiro;
        this.instituicao = instituicao;
        this.darf = darf;
        this.operacao = operacao;
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getEntradaSaida() {
        return entradaSaida;
    }

    public void setEntradaSaida(String entradaSaida) {
        this.entradaSaida = entradaSaida;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }

    public void setPrecoMedio(BigDecimal precoMedio) {
        this.precoMedio = precoMedio;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }

    public AtivoFinanceiro getAtivoFinanceiro() {
        return ativoFinanceiro;
    }

    public void setAtivoFinanceiro(AtivoFinanceiro ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public Darf getDarf() {
        return darf;
    }

    public void setDarf(Darf darf) {
        this.darf = darf;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}

