package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Infrastructure Entity - AtivoRendaFixaEntity (SINGLE_TABLE Strategy)
 * 
 * Representa ativos de renda fixa como:
 * - CDBs, LCIs, LCAs
 * - Tesouro Direto
 * - Debêntures
 * - Fundos de Renda Fixa
 * 
 * Características específicas:
 * - Taxa de juros definida
 * - Data de vencimento (opcional)
 * - Emissor conhecido
 * - Indexador (CDI, IPCA, etc.)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("RENDA_FIXA")
public class AtivoRendaFixaEntity extends AtivoFinanceiroEntity {
    
    /**
     * Tipo específico de renda fixa
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_renda_fixa")
    private TipoAtivoFinanceiroFixa tipoRendaFixa;
    
    /**
     * Taxa de juros anual do ativo
     */
    @Column(name = "taxa_juros", precision = 5, scale = 2)
    private BigDecimal taxaJuros;
    
    /**
     * Data de vencimento (pode ser null para ativos sem vencimento)
     */
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    
    /**
     * Indexador do ativo (CDI, IPCA, SELIC, etc.)
     */
    @Column(name = "indexador", length = 50)
    private String indexador;
    
    /**
     * Emissor do ativo
     */
    @Column(name = "emissor", length = 200)
    private String emissor;
    
    /**
     * Valor mínimo para aplicação
     */
    @Column(name = "valor_minimo", precision = 15, scale = 2)
    private BigDecimal valorMinimo;
    
    /**
     * Indica se o ativo tem liquidez diária
     */
    @Column(name = "liquidez_diaria")
    private Boolean liquidezDiaria;
    
    // Implementação dos métodos abstratos
    
    @Override
    public TipoAtivo getTipoAtivo() {
        return TipoAtivo.RENDA_FIXA;
    }
    
    @Override
    public String getDescricaoCompleta() {
        StringBuilder desc = new StringBuilder();
        desc.append(getNome());
        
        if (emissor != null) {
            desc.append(" - ").append(emissor);
        }
        
        if (tipoRendaFixa != null) {
            desc.append(" (").append(tipoRendaFixa).append(")");
        }
        
        if (taxaJuros != null) {
            desc.append(" - ").append(taxaJuros).append("%");
        }
        
        if (indexador != null) {
            desc.append(" ").append(indexador);
        }
        
        return desc.toString();
    }
    
    // Métodos específicos de Renda Fixa
    
    /**
     * Verifica se o ativo está vencido
     */
    public boolean isVencido() {
        return dataVencimento != null && dataVencimento.isBefore(LocalDate.now());
    }
    
    /**
     * Calcula quantos dias faltam para o vencimento
     */
    public long getDiasParaVencimento() {
        if (dataVencimento == null) {
            return Long.MAX_VALUE; // Sem vencimento
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dataVencimento);
    }
    
    /**
     * Calcula o rendimento projetado para um valor investido
     */
    public BigDecimal calcularRendimentoProjetado(BigDecimal valorInvestido) {
        if (taxaJuros == null || valorInvestido == null) {
            return BigDecimal.ZERO;
        }
        
        if (dataVencimento == null) {
            // Para ativos sem vencimento, calcular rendimento anual
            return valorInvestido.multiply(taxaJuros.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP));
        }
        
        long dias = getDiasParaVencimento();
        if (dias <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal taxaDiaria = taxaJuros.divide(BigDecimal.valueOf(36500), 8, java.math.RoundingMode.HALF_UP);
        return valorInvestido.multiply(taxaDiaria).multiply(BigDecimal.valueOf(dias));
    }
    
    /**
     * Verifica se é um ativo de alta liquidez
     */
    public boolean isAltaLiquidez() {
        return Boolean.TRUE.equals(liquidezDiaria);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AtivoRendaFixaEntity that = (AtivoRendaFixaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}