package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Infrastructure Entity - TransacaoEntity (Opção 1)
 * 
 * Representa o histórico completo de operações financeiras.
 * Separado da PosicaoEntity (estado atual) para otimizar consultas.
 * 
 * Características da Opção 1:
 * - Histórico imutável de operações
 * - Enums tipados para tipos de transação e movimentação
 * - Campos otimizados para auditoria
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transacao", indexes = {
    @Index(name = "idx_transacao_data", columnList = "data_operacao"),
    @Index(name = "idx_transacao_ativo", columnList = "ativo_financeiro_id"),
    @Index(name = "idx_transacao_portfolio", columnList = "portfolio_id"),
    @Index(name = "idx_transacao_tipo", columnList = "tipo_transacao")
})
public class TransacaoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data da operação
     */
    @Column(name = "data_operacao", nullable = false)
    private LocalDate dataOperacao;

    /**
     * Tipo da transação usando enum tipado
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false, length = 20)
    private TipoTransacao tipoTransacao;

    /**
     * Tipo da movimentação usando enum tipado
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    private TipoMovimentacao tipoMovimentacao;

    /**
     * Quantidade de ativos na transação
     */
    @Column(name = "quantidade", precision = 19, scale = 8, nullable = false)
    private BigDecimal quantidade;

    /**
     * Preço unitário da transação
     */
    @Column(name = "preco_unitario", precision = 19, scale = 8, nullable = false)
    private BigDecimal precoUnitario;

    /**
     * Valor total da transação (quantidade * precoUnitario)
     */
    @Column(name = "valor_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    /**
     * Taxas e custos da transação
     */
    @Column(name = "taxas", precision = 19, scale = 2)
    private BigDecimal taxas;

    /**
     * Valor líquido da transação (valorTotal - taxas)
     */
    @Column(name = "valor_liquido", precision = 19, scale = 2)
    private BigDecimal valorLiquido;

    /**
     * Observações sobre a transação
     */
    @Column(name = "observacoes", length = 500)
    private String observacoes;

    /**
     * Flag de controle para soft delete
     */
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    /**
     * Referência ao ativo financeiro
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_financeiro_id", nullable = false)
    private AtivoFinanceiroEntity ativoFinanceiro;

    /**
     * Referência à instituição
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituicao_id")
    private InstituicaoEntity instituicao;

    /**
     * Referência ao portfolio (agregado raiz)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    /**
     * Referência à operação original (opcional)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacao_id")
    private OperacaoEntity operacao;
    
    /**
     * Referência ao DARF que incluiu esta transação (opcional)
     * Preenchido quando a transação é incluída em um cálculo de imposto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "darf_id")
    private DarfEntity darf;

    // Métodos de ciclo de vida JPA
    
    @PrePersist
    protected void onCreate() {
        if (this.ativo == null) {
            this.ativo = true;
        }
        if (this.taxas == null) {
            this.taxas = BigDecimal.ZERO;
        }
        calcularValores();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calcularValores();
    }
    
    // Métodos de negócio da Opção 1
    
    /**
     * Calcula valores derivados da transação
     */
    private void calcularValores() {
        if (quantidade != null && precoUnitario != null) {
            this.valorTotal = quantidade.multiply(precoUnitario);
            this.valorLiquido = valorTotal.subtract(taxas != null ? taxas : BigDecimal.ZERO);
        }
    }
    
    /**
     * Verifica se é uma transação de compra
     */
    public boolean isCompra() {
        return TipoTransacao.ENTRADA.equals(tipoTransacao);
    }
    
    /**
     * Verifica se é uma transação de venda
     */
    public boolean isVenda() {
        return TipoTransacao.VENDA.equals(tipoTransacao);
    }
    
    /**
     * Verifica se é uma transação de rendimento
     */
    public boolean isRendimento() {
        return TipoTransacao.LUCRO_DIVIDENDO.equals(tipoTransacao) || 
               TipoTransacao.LUCRO_JUROS.equals(tipoTransacao) || 
               TipoTransacao.LUCRO_RENDIMENTO.equals(tipoTransacao);
    }
    
    /**
     * Associa um ativo financeiro à transação
     */
    public void associarAtivoFinanceiro(AtivoFinanceiroEntity ativoFinanceiro) {
        if (ativoFinanceiro == null) {
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo.");
        }
        this.ativoFinanceiro = ativoFinanceiro;
    }
    
    /**
     * Associa uma instituição à transação
     */
    public void associarInstituicao(InstituicaoEntity instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("Instituição não pode ser nula.");
        }
        this.instituicao = instituicao;
    }
    
    /**
     * Associa um DARF à transação
     */
    public void associarDarf(DarfEntity darf) {
        this.darf = darf;
    }
    
    /**
     * Getter para DARF
     */
    public DarfEntity getDarf() {
        return darf;
    }
    
    /**
     * Setter para DARF
     */
    public void setDarf(DarfEntity darf) {
        this.darf = darf;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TransacaoEntity transacao = (TransacaoEntity) o;
        return getId() != null && Objects.equals(getId(), transacao.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
