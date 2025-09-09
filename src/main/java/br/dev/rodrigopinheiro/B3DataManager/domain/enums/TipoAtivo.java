package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

import lombok.Getter;

/**
 * Enum para categorização geral dos tipos de ativos financeiros
 * Usado na Opção 1 para separar renda fixa de renda variável
 */
@Getter
public enum TipoAtivo {
    RENDA_FIXA("Renda Fixa", "Investimentos com rentabilidade previsível e menor risco"),
    RENDA_VARIAVEL("Renda Variável", "Investimentos com rentabilidade variável e maior potencial de ganho");
    
    private final String descricao;
    private final String detalhamento;
    
    TipoAtivo(String descricao, String detalhamento) {
        this.descricao = descricao;
        this.detalhamento = detalhamento;
    }
    
    /**
     * Determina o tipo geral baseado no tipo específico do ativo
     */
    public static TipoAtivo fromTipoEspecifico(String tipoEspecifico) {
        if (tipoEspecifico == null) {
            return null;
        }
        
        // Renda Variável
        if (tipoEspecifico.startsWith("ACAO_") || 
            "FII".equals(tipoEspecifico) || 
            "ETF".equals(tipoEspecifico) || 
            "BDR".equals(tipoEspecifico) ||
            tipoEspecifico.contains("DIREITO_") ||
            tipoEspecifico.contains("RECIBO_")) {
            return RENDA_VARIAVEL;
        }
        
        // Renda Fixa
        if ("TITULO_PUBLICO".equals(tipoEspecifico) ||
            "CDB".equals(tipoEspecifico) ||
            "LETRA_FINANCEIRA".equals(tipoEspecifico) ||
            "LCI".equals(tipoEspecifico) ||
            "LCA".equals(tipoEspecifico) ||
            "DEBENTURE".equals(tipoEspecifico)) {
            return RENDA_FIXA;
        }
        
        // Default para desconhecidos
        return null;
    }
}