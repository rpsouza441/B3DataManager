package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;

/**
 * Domain Model - AtivoFinanceiro (SINGLE_TABLE Strategy)
 * 
 * Classe abstrata base para hierarquia de ativos financeiros.
 * Implementa padrão SINGLE_TABLE para máxima performance.
 * 
 * Subclasses:
 * - AtivoRendaFixa: CDBs, Tesouro Direto, LCI/LCA, etc.
 * - AtivoRendaVariavel: Ações, FIIs, ETFs, BDRs, etc.
 * 
 * Princípios:
 * - POJO puro sem dependências externas
 * - Type safety através de herança
 * - Polimorfismo para operações genéricas
 * - Métodos abstratos para comportamentos específicos
 */
public abstract class AtivoFinanceiro {
    private Long id;

    /**
     * Código do ativo (ticker para ações/FIIs, código para RF)
     */
    private String codigo;

    /**
     * Nome completo do ativo
     */
    private String nome;

    /**
     * Referência ao portfolio (objeto completo)
     */
    private Portfolio portfolio;

    /**
     * Flag de controle para soft delete
     */
    private Boolean deletado = false;

    public AtivoFinanceiro() {
        this.deletado = false;
    }

    public AtivoFinanceiro(String codigo, String nome, Portfolio portfolio) {
        this.codigo = codigo;
        this.nome = nome;
        this.portfolio = portfolio;
        this.deletado = false;
    }

    // Métodos abstratos para polimorfismo

    /**
     * Retorna o tipo geral do ativo.
     * 
     * <p>
     * Determina se o ativo é de Renda Fixa ou Renda Variável.
     * </p>
     * 
     * <p>
     * <b>Implementações:</b>
     * </p>
     * <ul>
     * <li>{@link AtivoRendaVariavel}: Retorna {@code TipoAtivo.RENDA_VARIAVEL}</li>
     * <li>{@link AtivoRendaFixa}: Retorna {@code TipoAtivo.RENDA_FIXA}</li>
     * </ul>
     * 
     * @return Tipo do ativo (RENDA_FIXA ou RENDA_VARIAVEL)
     * @see TipoAtivo
     */
    public abstract TipoAtivo getTipoAtivo();

    /**
     * Retorna uma descrição completa e detalhada do ativo.
     * 
     * <p>
     * Inclui informações específicas do tipo de ativo:
     * </p>
     * <ul>
     * <li><b>Renda Variável:</b> Ticker, setor, tipo (ação/FII/ETF)</li>
     * <li><b>Renda Fixa:</b> Vencimento, indexador, taxa</li>
     * </ul>
     * 
     * <p>
     * <b>Exemplo de saída:</b>
     * </p>
     * 
     * <pre>
     * "PETR4 (Ação) - Petrobras PN - Setor: Petróleo"
     * "Tesouro IPCA+ 2035 - Vencimento: 15/05/2035 - Taxa: 5.5% + IPCA"
     * </pre>
     * 
     * @return Descrição formatada do ativo
     */
    public abstract String getDescricaoCompleta();

    /**
     * Valida se todos os campos obrigatórios do ativo estão preenchidos.
     * 
     * <p>
     * Cada tipo de ativo tem suas próprias validações:
     * </p>
     * <ul>
     * <li><b>Todos:</b> código, nome não podem ser nulos</li>
     * <li><b>Renda Variável:</b> tipoRendaVariavel obrigatório</li>
     * <li><b>Renda Fixa:</b> tipoRendaFixa, taxaJuros, emissor obrigatórios</li>
     * </ul>
     * 
     * @throws IllegalStateException se algum campo obrigatório estiver nulo ou
     *                               inválido
     */
    public abstract void validarCamposObrigatorios();

    // Métodos de negócio comuns

    /**
     * Verifica se é um ativo de renda variável
     */
    public boolean isRendaVariavel() {
        return TipoAtivo.RENDA_VARIAVEL.equals(getTipoAtivo());
    }

    /**
     * Verifica se é um ativo de renda fixa
     */
    public boolean isRendaFixa() {
        return TipoAtivo.RENDA_FIXA.equals(getTipoAtivo());
    }

    /**
     * Retorna uma representação string do ativo
     */
    @Override
    public String toString() {
        return String.format("%s [%s] - %s",
                codigo, getTipoAtivo(), nome);
    }

    /**
     * Implementação de equals baseada no código do ativo
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        AtivoFinanceiro that = (AtivoFinanceiro) obj;
        return codigo != null ? codigo.equals(that.codigo) : that.codigo == null;
    }

    /**
     * Implementação de hashCode baseada no código do ativo
     */
    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
}
