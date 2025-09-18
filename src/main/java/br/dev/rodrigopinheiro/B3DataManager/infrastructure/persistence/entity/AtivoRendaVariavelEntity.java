package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Infrastructure Entity - AtivoRendaVariavelEntity (SINGLE_TABLE Strategy)
 * 
 * Representa ativos de renda variável como:
 * - Ações (ACAO)
 * - Fundos Imobiliários (FII)
 * - ETFs (ETF)
 * - BDRs (BDR)
 * 
 * Características específicas:
 * - Preço variável de mercado
 * - Dividend yield (para ações e FIIs)
 * - Setor e segmento econômico
 * - Ticker para cotações
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("RENDA_VARIAVEL")
public class AtivoRendaVariavelEntity extends AtivoFinanceiroEntity {
    
    /**
     * Tipo específico de renda variável
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_renda_variavel")
    private TipoAtivoFinanceiroVariavel tipoRendaVariavel;
    
    /**
     * Setor econômico do ativo
     */
    @Column(name = "setor", length = 100)
    private String setor;
    
    /**
     * Segmento específico dentro do setor
     */
    @Column(name = "segmento", length = 100)
    private String segmento;
    
    @Column(name = "ticker", length = 20)
    private String ticker;
    
    /**
     * Dividend yield anual (percentual)
     */
    @Column(name = "dividend_yield", precision = 5, scale = 4)
    private BigDecimal dividendYield;
    
    /**
     * Percentual de ações em circulação (free float)
     */
    @Column(name = "free_float", precision = 5, scale = 2)
    private BigDecimal freeFloat;
    
    /**
     * Valor de mercado da empresa (market cap)
     */
    @Column(name = "market_cap")
    private Long marketCap;
    
    // Implementação dos métodos abstratos
    
    @Override
    public TipoAtivo getTipoAtivo() {
        return TipoAtivo.RENDA_VARIAVEL;
    }
    
    @Override
    public String getDescricaoCompleta() {
        StringBuilder desc = new StringBuilder();
        desc.append(getNome());
        
        if (getCodigo() != null) {
            desc.append(" (").append(getCodigo()).append(")");
        }
        
        if (tipoRendaVariavel != null) {
            desc.append(" - ").append(tipoRendaVariavel);
        }
        
        if (setor != null) {
            desc.append(" - ").append(setor);
        }
        
        if (dividendYield != null && dividendYield.compareTo(BigDecimal.ZERO) > 0) {
            desc.append(" - DY: ").append(dividendYield.multiply(BigDecimal.valueOf(100))).append("%");
        }
        
        return desc.toString();
    }
    
    // Métodos específicos de Renda Variável (delegam para o enum)
    
    /**
     * Verifica se é uma ação (delega para o enum)
     */
    public boolean isAcao() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isAcao();
    }
    
    /**
     * Verifica se é um FII (delega para o enum)
     */
    public boolean isFII() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isFII();
    }
    
    /**
     * Verifica se é um ETF (delega para o enum)
     */
    public boolean isETF() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isETF();
    }
    
    /**
     * Verifica se é um BDR (delega para o enum)
     */
    public boolean isBDR() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isBDR();
    }
    
    /**
     * Verifica se é uma ação ordinária (delega para o enum)
     */
    public boolean isAcaoOrdinaria() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isAcaoOrdinaria();
    }
    
    /**
     * Verifica se é uma ação preferencial (delega para o enum)
     */
    public boolean isAcaoPreferencial() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isAcaoPreferencial();
    }
    
    /**
     * Verifica se é um ativo derivado (delega para o enum)
     */
    public boolean isDerivado() {
        return tipoRendaVariavel != null && tipoRendaVariavel.isDerivado();
    }
    
    /**
     * Verifica se paga dividendos (delega para o enum)
     */
    public boolean pagaDividendos() {
        return tipoRendaVariavel != null && tipoRendaVariavel.pagaDividendos();
    }
    
    /**
     * Retorna a categoria do ativo (delega para o enum)
     */
    public String getCategoria() {
        return tipoRendaVariavel != null ? tipoRendaVariavel.getCategoria() : "Desconhecido";
    }
    
    /**
     * Retorna o ticker formatado para consultas (código + .SA para ações brasileiras)
     */
    public String getTickerFormatado() {
        return getCodigo() != null ? getCodigo() + ".SA" : null;
    }
    
    /**
     * Verifica se tem dividend yield alto (> 6%)
     */
    public boolean isHighDividendYield() {
        return dividendYield != null && 
               dividendYield.compareTo(BigDecimal.valueOf(0.06)) > 0;
    }
    
    /**
     * Verifica se é uma empresa de grande capitalização (> 10B)
     */
    public boolean isLargeCap() {
        return marketCap != null && marketCap > 10_000_000_000L;
    }
    
    /**
     * Verifica se é uma empresa de média capitalização (1B - 10B)
     */
    public boolean isMidCap() {
        return marketCap != null && 
               marketCap >= 1_000_000_000L && 
               marketCap <= 10_000_000_000L;
    }
    
    /**
     * Verifica se é uma empresa de pequena capitalização (< 1B)
     */
    public boolean isSmallCap() {
        return marketCap != null && marketCap < 1_000_000_000L;
    }
    
    /**
     * Calcula o dividend yield anual em reais para um valor investido
     */
    public BigDecimal calcularDividendoAnual(BigDecimal valorInvestido, BigDecimal precoAtual) {
        if (dividendYield == null || valorInvestido == null || precoAtual == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal quantidade = valorInvestido.divide(precoAtual, 8, java.math.RoundingMode.HALF_UP);
        return quantidade.multiply(precoAtual).multiply(dividendYield);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AtivoRendaVariavelEntity that = (AtivoRendaVariavelEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}