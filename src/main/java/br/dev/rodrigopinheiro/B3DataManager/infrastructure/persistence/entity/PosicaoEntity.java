package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Infrastructure Entity - PosicaoEntity (Opção 1)
 * 
 * Representa o estado atual de um ativo financeiro no portfolio.
 * Separado do histórico de transações para otimizar consultas.
 * 
 * Características da Opção 1:
 * - Coluna percentual_carteira pré-calculada (performance)
 * - Estado atual vs histórico (TransacaoEntity)
 * - JPA Entity para persistência
 */
@Entity
@Table(name = "posicao", indexes = {
    @Index(name = "idx_posicao_ativo_portfolio", columnList = "ativo_financeiro_id, portfolio_id"),
    @Index(name = "idx_posicao_portfolio", columnList = "portfolio_id"),
    @Index(name = "idx_posicao_ativo", columnList = "ativo_financeiro_id")
})
public class PosicaoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Quantidade atual do ativo (resultado de todas as transações)
     */
    @Column(name = "quantidade_atual", precision = 19, scale = 8, nullable = false)
    private BigDecimal quantidadeAtual;
    
    /**
     * Preço médio de aquisição (ponderado pelas compras)
     */
    @Column(name = "preco_medio", precision = 19, scale = 8, nullable = false)
    private BigDecimal precoMedio;
    
    /**
     * Valor atual da posição (quantidade * preço atual de mercado)
     */
    @Column(name = "valor_atual", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorAtual;
    
    /**
     * COLUNA CHAVE DA OPÇÃO 1: Percentual pré-calculado para performance
     * Evita cálculo em tempo real nas views
     */
    @Column(name = "percentual_carteira", precision = 5, scale = 2)
    private BigDecimal percentualPortfolio;
    
    /**
     * Data da última atualização da posição
     */
    @Column(name = "data_ultima_atualizacao")
    private LocalDate dataUltimaAtualizacao;
    
    /**
     * Lucro/prejuízo não realizado (valor atual - valor investido)
     */
    @Column(name = "lucro_nao_realizado", precision = 19, scale = 2)
    private BigDecimal lucroNaoRealizado;
    
    /**
     * Valor total investido na posição (soma das compras - vendas)
     */
    @Column(name = "valor_investido", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorInvestido;
    
    /**
     * Referência ao ativo financeiro
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_financeiro_id", nullable = false)
    private AtivoFinanceiroEntity ativoFinanceiro;
    
    /**
     * Referência ao portfolio
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;
    
    /**
     * Flag de controle para soft delete
     */
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    /**
     * Controle de auditoria
     */
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    // Construtores
    public PosicaoEntity() {
        this.quantidadeAtual = BigDecimal.ZERO;
        this.precoMedio = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
        this.percentualPortfolio = BigDecimal.ZERO;
        this.lucroNaoRealizado = BigDecimal.ZERO;
        this.valorInvestido = BigDecimal.ZERO;
        this.ativo = true;
        this.dataUltimaAtualizacao = LocalDate.now();
    }
    
    public PosicaoEntity(AtivoFinanceiroEntity ativoFinanceiro, PortfolioEntity portfolio) {
        this();
        this.ativoFinanceiro = ativoFinanceiro;
        this.portfolio = portfolio;
    }
    
    // Métodos de ciclo de vida JPA
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        if (this.dataUltimaAtualizacao == null) {
            this.dataUltimaAtualizacao = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
        this.dataUltimaAtualizacao = LocalDate.now();
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }
    
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }
    
    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }
    
    public void setPrecoMedio(BigDecimal precoMedio) {
        this.precoMedio = precoMedio;
    }
    
    public BigDecimal getValorAtual() {
        return valorAtual;
    }
    
    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }
    
    public BigDecimal getPercentualPortfolio() {
        return percentualPortfolio;
    }
    
    public void setPercentualPortfolio(BigDecimal percentualPortfolio) {
        this.percentualPortfolio = percentualPortfolio;
    }
    
    public LocalDate getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }
    
    public void setDataUltimaAtualizacao(LocalDate dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }
    
    public BigDecimal getLucroNaoRealizado() {
        return lucroNaoRealizado;
    }
    
    public void setLucroNaoRealizado(BigDecimal lucroNaoRealizado) {
        this.lucroNaoRealizado = lucroNaoRealizado;
    }
    
    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }
    
    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }
    
    public AtivoFinanceiroEntity getAtivoFinanceiro() {
        return ativoFinanceiro;
    }
    
    public void setAtivoFinanceiro(AtivoFinanceiroEntity ativoFinanceiro) {
        this.ativoFinanceiro = ativoFinanceiro;
    }
    
    public PortfolioEntity getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}