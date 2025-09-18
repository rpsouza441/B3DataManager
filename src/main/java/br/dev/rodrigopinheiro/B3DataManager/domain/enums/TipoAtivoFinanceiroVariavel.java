package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

import lombok.Getter;

@Getter
public enum TipoAtivoFinanceiroVariavel {
    ACAO_ON("Ação Ordinária", "Ação que confere ao acionista o direito de voto em assembleias e participação nos resultados da empresa."),
    ACAO_PN("Ação Preferencial", "Ação que dá prioridade no recebimento de dividendos, porém geralmente sem direito a voto."),
    ACAO_PNA("Ação Preferencial Classe A", "Ação preferencial com prioridade sobre outras classes."),
    ACAO_PNB("Ação Preferencial Classe B", "Ação preferencial com prioridade inferior à classe A."),
    ACAO_PNC("Ação Preferencial Classe C", "Ação preferencial com menor prioridade, mas com garantia de retorno financeiro."),
    ACAO_PND("Ação Preferencial Classe D", "Ação preferencial com menos direitos que outras classes."),
    ACAO_UNIT("Unit", "Pacote de ativos que combina ações ordinárias e preferenciais de uma empresa."),
    FII("Fundo de Investimento Imobiliário", "Fundo que investe em empreendimentos imobiliários, permitindo que investidores participem do mercado imobiliário sem adquirir imóveis diretamente."),
    ETF("Fundo de Índice", "Fundo que busca replicar o desempenho de um índice de mercado, permitindo diversificação com a compra de uma única cota."),
    BDR("Brazilian Depositary Receipt", "Certificado que representa ações de empresas estrangeiras, permitindo que investidores brasileiros invistam em companhias internacionais."),
    DIREITO_SUBSCRICAO_ON("Direito de Subscrição de Ação Ordinária", "Direito que permite ao acionista adquirir novas ações ordinárias emitidas pela empresa antes de serem oferecidas ao mercado."),
    DIREITO_SUBSCRICAO_PN("Direito de Subscrição de Ação Preferencial", "Direito que permite ao acionista adquirir novas ações preferenciais emitidas pela empresa antes de serem oferecidas ao mercado."),
    RECIBO_SUBSCRICAO_ON("Recibo de Subscrição de Ação Ordinária", "Comprovante de que o acionista exerceu seu direito de subscrição para adquirir ações ordinárias."),
    RECIBO_SUBSCRICAO_PN("Recibo de Subscrição de Ação Preferencial", "Comprovante de que o acionista exerceu seu direito de subscrição para adquirir ações preferenciais."),
    DESCONHECIDO("Tipo Desconhecido", "Tipo de ativo não identificado ou não reconhecido.");

    private final String descricaoRapida;
    private final String descricaoDetalhada;

    TipoAtivoFinanceiroVariavel(String descricaoRapida, String descricaoDetalhada) {
        this.descricaoRapida = descricaoRapida;
        this.descricaoDetalhada = descricaoDetalhada;
    }
    
    // Métodos de classificação (seguindo SOLID - Single Responsibility)
    
    /**
     * Verifica se o tipo representa uma ação (qualquer classe)
     */
    public boolean isAcao() {
        return this == ACAO_ON || this == ACAO_PN || this == ACAO_PNA || 
               this == ACAO_PNB || this == ACAO_PNC || this == ACAO_PND || this == ACAO_UNIT;
    }
    
    /**
     * Verifica se é uma ação ordinária
     */
    public boolean isAcaoOrdinaria() {
        return this == ACAO_ON;
    }
    
    /**
     * Verifica se é uma ação preferencial (qualquer classe)
     */
    public boolean isAcaoPreferencial() {
        return this == ACAO_PN || this == ACAO_PNA || this == ACAO_PNB || 
               this == ACAO_PNC || this == ACAO_PND;
    }
    
    /**
     * Verifica se é um Fundo de Investimento Imobiliário
     */
    public boolean isFII() {
        return this == FII;
    }
    
    /**
     * Verifica se é um ETF (Exchange Traded Fund)
     */
    public boolean isETF() {
        return this == ETF;
    }
    
    /**
     * Verifica se é um BDR (Brazilian Depositary Receipt)
     */
    public boolean isBDR() {
        return this == BDR;
    }
    
    /**
     * Verifica se é um direito de subscrição (qualquer tipo)
     */
    public boolean isDireitoSubscricao() {
        return this == DIREITO_SUBSCRICAO_ON || this == DIREITO_SUBSCRICAO_PN;
    }
    
    /**
     * Verifica se é um recibo de subscrição (qualquer tipo)
     */
    public boolean isReciboSubscricao() {
        return this == RECIBO_SUBSCRICAO_ON || this == RECIBO_SUBSCRICAO_PN;
    }
    
    /**
     * Verifica se é um ativo derivado (direitos e recibos)
     */
    public boolean isDerivado() {
        return isDireitoSubscricao() || isReciboSubscricao();
    }
    
    /**
     * Verifica se é um ativo principal (ações, FIIs, ETFs, BDRs)
     */
    public boolean isAtivoPrincipal() {
        return isAcao() || isFII() || isETF() || isBDR();
    }
    
    /**
     * Verifica se é um tipo desconhecido
     */
    public boolean isDesconhecido() {
        return this == DESCONHECIDO;
    }
    
    /**
     * Retorna a categoria principal do ativo
     */
    public String getCategoria() {
        if (isAcao()) return "Ação";
        if (isFII()) return "FII";
        if (isETF()) return "ETF";
        if (isBDR()) return "BDR";
        if (isDireitoSubscricao()) return "Direito";
        if (isReciboSubscricao()) return "Recibo";
        return "Desconhecido";
    }
    
    /**
     * Verifica se o ativo paga dividendos regulares
     */
    public boolean pagaDividendos() {
        return isAcao() || isFII();
    }
    
    /**
     * Verifica se o ativo tem liquidez diária
     */
    public boolean temLiquidezDiaria() {
        return isAtivoPrincipal(); // Ações, FIIs, ETFs e BDRs têm liquidez diária
    }

}