package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.StatusDarf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Darf {
    private Long id;

    private boolean estaPago;

    private LocalDate dataPagamento;

    private BigDecimal valor;

    // Auditoria básica
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relacionamento com usuário
    private Long usuarioId;

    // Período de referência
    private Integer mesReferencia;
    private Integer anoReferencia;

    // Status do DARF
    private StatusDarf status;

    // Data de vencimento
    private LocalDate dataVencimento;

    private List<Transacao> transacoes;

    public Darf() {
    }

    public Darf(Long id, boolean estaPago, LocalDate dataPagamento, BigDecimal valor, 
                LocalDateTime createdAt, LocalDateTime updatedAt, Long usuarioId, 
                Integer mesReferencia, Integer anoReferencia, StatusDarf status, 
                LocalDate dataVencimento, List<Transacao> transacoes) {
        this.id = id;
        this.estaPago = estaPago;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.usuarioId = usuarioId;
        this.mesReferencia = mesReferencia;
        this.anoReferencia = anoReferencia;
        this.status = status;
        this.dataVencimento = dataVencimento;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Integer mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public Integer getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(Integer anoReferencia) {
        this.anoReferencia = anoReferencia;
    }

    public StatusDarf getStatus() {
        return status;
    }

    public void setStatus(StatusDarf status) {
        this.status = status;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}
