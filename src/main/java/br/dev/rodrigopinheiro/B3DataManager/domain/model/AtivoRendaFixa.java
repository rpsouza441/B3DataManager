package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domain Model - AtivoRendaFixa (SINGLE_TABLE Strategy)
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
public class AtivoRendaFixa extends AtivoFinanceiro {

    /**
     * Tipo específico de renda fixa
     */
    private TipoAtivoFinanceiroFixa tipoRendaFixa;

    /**
     * Taxa de juros anual do ativo
     */
    private BigDecimal taxaJuros;

    /**
     * Data de vencimento (pode ser null para ativos sem vencimento)
     */
    private LocalDate dataVencimento;

    /**
     * Indexador do ativo (CDI, IPCA, SELIC, etc.)
     */
    private String indexador;

    /**
     * Emissor do ativo
     */
    private String emissor;

    /**
     * Valor mínimo para aplicação
     */
    private BigDecimal valorMinimo;

    /**
     * Indica se o ativo tem liquidez diária
     */
    private Boolean liquidezDiaria;

    public AtivoRendaFixa() {
        super();
    }

    public AtivoRendaFixa(String codigo, String nome, Portfolio portfolio,
            TipoAtivoFinanceiroFixa tipoRendaFixa, BigDecimal taxaJuros, String emissor) {
        super(codigo, nome, portfolio);
        this.tipoRendaFixa = tipoRendaFixa;
        this.taxaJuros = taxaJuros;
        this.emissor = emissor;
        this.liquidezDiaria = false;
    }

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

    @Override
    public void validarCamposObrigatorios() {
        if (tipoRendaFixa == null) {
            throw new IllegalStateException("Tipo de Renda Fixa é obrigatório");
        }
        if (taxaJuros == null) {
            throw new IllegalStateException("Taxa de juros é obrigatória para Renda Fixa");
        }
        if (emissor == null || emissor.trim().isEmpty()) {
            throw new IllegalStateException("Emissor é obrigatório para Renda Fixa");
        }
    }

    // Métodos específicos de Renda Fixa

    /**
     * Verifica se o ativo já venceu.
     * 
     * <p>
     * Compara a data de vencimento com a data atual.
     * </p>
     * 
     * <p>
     * <b>Uso:</b> Identificar ativos que precisam ser resgatados ou já foram pagos.
     * </p>
     * 
     * @return {@code true} se já venceu, {@code false} caso contrário
     * @see #getDataVencimento()
     * @see #getDiasParaVencimento()
     */
    public boolean isVencido() {
        return dataVencimento != null && dataVencimento.isBefore(LocalDate.now());
    }

    /**
     * Calcula quantos dias faltam até o vencimento.
     * 
     * <p>
     * Retorna valor negativo se já venceu.
     * </p>
     * 
     * <p>
     * <b>Exemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * long dias = ativo.getDiasParaVencimento();
     * if (dias > 0) {
     *     System.out.println("Faltam " + dias + " dias para o vencimento");
     * } else {
     *     System.out.println("Já venceu há " + Math.abs(dias) + " dias");
     * }
     * }</pre>
     * 
     * @return Número de dias até o vencimento (negativo se já venceu)
     * @see #isVencido()
     */
    public long getDiasParaVencimento() {
        if (dataVencimento == null) {
            return Long.MAX_VALUE; // Sem vencimento
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dataVencimento);
    }

    /**
     * Calcula o rendimento projetado até o vencimento.
     * 
     * <p>
     * Considera a taxa prefixada ou indexador + taxa.
     * </p>
     * 
     * <p>
     * <b>⚠️ Importante:</b> Para títulos indexados (IPCA, CDI),
     * o cálculo é aproximado pois não projeta o índice futuro.
     * </p>
     * 
     * <p>
     * <b>Fórmula simplificada:</b>
     * </p>
     * 
     * <pre>{@code
     * rendimento = valorInvestido × (taxaJuros / 100) × (dias / 365)
     * }</pre>
     * 
     * @param valorInvestido Valor aplicado no título
     * @return Rendimento estimado bruto (antes de IR)
     */
    public BigDecimal calcularRendimentoProjetado(BigDecimal valorInvestido) {
        if (taxaJuros == null || valorInvestido == null) {
            return BigDecimal.ZERO;
        }

        if (dataVencimento == null) {
            // Para ativos sem vencimento, calcular rendimento anual
            return valorInvestido.multiply(taxaJuros.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        }

        long dias = getDiasParaVencimento();
        if (dias <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxaDiaria = taxaJuros.divide(BigDecimal.valueOf(36500), 8, RoundingMode.HALF_UP);
        return valorInvestido.multiply(taxaDiaria).multiply(BigDecimal.valueOf(dias));
    }

    /**
     * Verifica se é um ativo de alta liquidez
     */
    public boolean isAltaLiquidez() {
        return Boolean.TRUE.equals(liquidezDiaria);
    }

    // Getters e Setters

    public TipoAtivoFinanceiroFixa getTipoRendaFixa() {
        return tipoRendaFixa;
    }

    public void setTipoRendaFixa(TipoAtivoFinanceiroFixa tipoRendaFixa) {
        this.tipoRendaFixa = tipoRendaFixa;
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getIndexador() {
        return indexador;
    }

    public void setIndexador(String indexador) {
        this.indexador = indexador;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Boolean getLiquidezDiaria() {
        return liquidezDiaria;
    }

    public void setLiquidezDiaria(Boolean liquidezDiaria) {
        this.liquidezDiaria = liquidezDiaria;
    }
}