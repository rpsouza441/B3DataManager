package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "posicao", indexes = {
    @Index(name = "idx_posicao_ativo_portfolio", columnList = "ativo_financeiro_id, portfolio_id"),
    @Index(name = "idx_posicao_portfolio", columnList = "portfolio_id"),
    @Index(name = "idx_posicao_ativo", columnList = "ativo_financeiro_id")
})
public class PosicaoEntity extends AuditableEntity {
    
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
     * Percentual de ganho/perda da posição ((valor atual - valor investido) / valor investido * 100)
     */
    @Column(name = "percentual_ganho", precision = 5, scale = 2)
    private BigDecimal percentualGanho;

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
    

    
    // Construtor customizado
    public PosicaoEntity(AtivoFinanceiroEntity ativoFinanceiro, PortfolioEntity portfolio) {
        this.ativoFinanceiro = ativoFinanceiro;
        this.portfolio = portfolio;
        this.quantidadeAtual = BigDecimal.ZERO;
        this.precoMedio = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
        this.percentualPortfolio = BigDecimal.ZERO;
        this.lucroNaoRealizado = BigDecimal.ZERO;
        this.percentualGanho = BigDecimal.ZERO;
        this.valorInvestido = BigDecimal.ZERO;
        this.ativo = true;
        this.dataUltimaAtualizacao = LocalDate.now();
    }
}