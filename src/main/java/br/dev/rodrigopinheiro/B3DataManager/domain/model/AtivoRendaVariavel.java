package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain Model - AtivoRendaVariavel (SINGLE_TABLE Strategy)
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
public class AtivoRendaVariavel extends AtivoFinanceiro {
    
    /**
     * Tipo específico de renda variável
     */
    private TipoAtivoFinanceiroVariavel tipoRendaVariavel;
    
    /**
     * Setor econômico do ativo
     */
    private String setor;
    
    /**
     * Segmento específico dentro do setor
     */
    private String segmento;
    
    /**
     * Ticker para consulta de cotações (ex: PETR4.SA)
     */
    private String ticker;
    
    /**
     * Dividend yield anual (percentual)
     */
    private BigDecimal dividendYield;
    
    /**
     * Percentual de ações em circulação (free float)
     */
    private BigDecimal freeFloat;
    
    /**
     * Valor de mercado da empresa (market cap)
     */
    private Long marketCap;
    
    public AtivoRendaVariavel() {
        super();
    }
    
    public AtivoRendaVariavel(String codigo, String nome, Portfolio portfolio, 
                             TipoAtivoFinanceiroVariavel tipoRendaVariavel, String setor) {
        super(codigo, nome, portfolio);
        this.tipoRendaVariavel = tipoRendaVariavel;
        this.setor = setor;
    }
    
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
    
    @Override
    public void validarCamposObrigatorios() {
        if (tipoRendaVariavel == null) {
            throw new IllegalStateException("Tipo de Renda Variável é obrigatório");
        }
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
        
        BigDecimal quantidade = valorInvestido.divide(precoAtual, 8, RoundingMode.HALF_UP);
        return quantidade.multiply(precoAtual).multiply(dividendYield);
    }
    
    // Getters e Setters
    
    public TipoAtivoFinanceiroVariavel getTipoRendaVariavel() {
        return tipoRendaVariavel;
    }
    
    public void setTipoRendaVariavel(TipoAtivoFinanceiroVariavel tipoRendaVariavel) {
        this.tipoRendaVariavel = tipoRendaVariavel;
    }
    
    public String getSetor() {
        return setor;
    }
    
    public void setSetor(String setor) {
        this.setor = setor;
    }
    
    public String getSegmento() {
        return segmento;
    }
    
    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    
    public BigDecimal getDividendYield() {
        return dividendYield;
    }
    
    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }
    
    public BigDecimal getFreeFloat() {
        return freeFloat;
    }
    
    public void setFreeFloat(BigDecimal freeFloat) {
        this.freeFloat = freeFloat;
    }
    
    public Long getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }
}