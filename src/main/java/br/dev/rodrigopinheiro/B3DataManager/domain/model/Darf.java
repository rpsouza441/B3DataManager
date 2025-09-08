package br.dev.rodrigopinheiro.B3DataManager.domain.model;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Darf {
    private Long id;

    private boolean estaPago;

    private LocalDate dataPagamento;

    private BigDecimal valor;

    private List<Transacao> transacoes;

    public Darf() {
    }

    public Darf(Long id, boolean estaPago, LocalDate dataPagamento, BigDecimal valor, List<Transacao> transacoes) {
        this.id = id;
        this.estaPago = estaPago;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.transacoes = transacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEstaPago() {
        return estaPago;
    }

    public void setEstaPago(boolean estaPago) {
        this.estaPago = estaPago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}
