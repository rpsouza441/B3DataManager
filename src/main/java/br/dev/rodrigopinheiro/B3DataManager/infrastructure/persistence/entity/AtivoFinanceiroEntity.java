package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Infrastructure Entity - AtivoFinanceiroEntity (Arquitetura Corrigida)
 * 
 * Representa um ativo financeiro unificado que pode ser:
 * - Renda Variável (ACAO, FII, ETF)
 * - Renda Fixa (CDB, LCI, TESOURO)
 * 
 * Características da arquitetura corrigida:
 * - Uso de enums tipados (TipoAtivo)
 * - Type safety completa
 * - Sem propriedades genéricas (Map<String, Object>)
 * - JPA Entity para persistência
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ativo_financeiro", indexes = {
    @Index(name = "idx_ativo_codigo", columnList = "codigo"),
    @Index(name = "idx_ativo_tipo", columnList = "tipo_ativo"),
    @Index(name = "idx_ativo_portfolio", columnList = "portfolio_id")
})
public class AtivoFinanceiroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Código do ativo (ticker para ações/FIIs, código para RF)
     */
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    /**
     * Nome completo do ativo
     */
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    /**
     * Tipo do ativo (RENDA_FIXA, RENDA_VARIAVEL)
     * Enum tipado para classificação segura
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ativo", nullable = false, length = 20)
    private TipoAtivo tipoAtivo;

    /**
     * Referência ao portfolio
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;

    /**
     * Transações relacionadas ao ativo (Opção 1: histórico)
     */
    @OneToMany(mappedBy = "ativoFinanceiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita loops
    private List<TransacaoEntity> transacoes = new ArrayList<>();

    /**
     * Posições atuais do ativo (Opção 1: estado atual)
     */
    @OneToMany(mappedBy = "ativoFinanceiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita loops
    private List<PosicaoEntity> posicoes = new ArrayList<>();

    /**
     * Flag de controle para soft delete
     */
    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;

    /**
     * Métodos de negócio
     */
    
    /**
     * Verifica se é um ativo de renda variável
     */
    public boolean isRendaVariavel() {
        return TipoAtivo.RENDA_VARIAVEL.equals(tipoAtivo);
    }
    
    /**
     * Verifica se é um ativo de renda fixa
     */
    public boolean isRendaFixa() {
        return TipoAtivo.RENDA_FIXA.equals(tipoAtivo);
    }
    
    /**
     * Adiciona uma transação ao ativo financeiro
     */
    public void adicionarTransacao(TransacaoEntity transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transação não pode ser nula.");
        }
        transacao.setAtivoFinanceiro(this);
        if (transacoes == null) {
            transacoes = new ArrayList<>();
        }
        transacoes.add(transacao);
    }
    
    /**
     * Adiciona uma posição ao ativo financeiro
     */
    public void adicionarPosicao(PosicaoEntity posicao) {
        if (posicao == null) {
            throw new IllegalArgumentException("Posição não pode ser nula.");
        }
        posicao.setAtivoFinanceiro(this);
        if (posicoes == null) {
            posicoes = new ArrayList<>();
        }
        posicoes.add(posicao);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AtivoFinanceiroEntity that = (AtivoFinanceiroEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}