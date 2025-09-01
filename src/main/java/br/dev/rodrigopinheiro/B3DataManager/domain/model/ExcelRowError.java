package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import java.util.Map;

/**
 * Representa uma linha do Excel que contém erro durante a importação.
 * 
 * <p>Esta classe encapsula tanto a informação do erro quanto os dados originais
 * da linha, permitindo que o usuário possa corrigir os dados e reimportar.</p>
 * 
 * <p>Benefícios desta abordagem:</p>
 * <ul>
 *   <li>Preserva os dados originais para correção</li>
 *   <li>Facilita a geração de relatórios de erro detalhados</li>
 *   <li>Permite correção direta no arquivo de erro</li>
 *   <li>Melhora significativamente a experiência do usuário</li>
 * </ul>
 * 
 * <p>Estrutura dos dados originais:</p>
 * <ul>
 *   <li>Chave: Nome da coluna (ex: "Data", "Produto", "Quantidade")</li>
 *   <li>Valor: Valor original da célula como string</li>
 *   <li>Ordem: Mantida conforme ordem das colunas no Excel</li>
 * </ul>
 * 
 * @param rowNumber Número da linha no Excel (1-indexed, incluindo cabeçalho)
 * @param errorMessage Mensagem descritiva do erro encontrado
 * @param originalData Dados originais da linha como mapa ordenado
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
public record ExcelRowError(
    int rowNumber,
    String errorMessage,
    Map<String, String> originalData
) {
    
    /**
     * Construtor que valida os parâmetros.
     */
    public ExcelRowError {
        if (rowNumber <= 0) {
            throw new IllegalArgumentException("Número da linha deve ser positivo");
        }
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem de erro é obrigatória");
        }
        if (originalData == null) {
            throw new IllegalArgumentException("Dados originais não podem ser nulos");
        }
    }
    
    /**
     * Cria um erro com dados originais extraídos de uma linha do Excel.
     * 
     * @param rowNumber Número da linha
     * @param errorMessage Mensagem de erro
     * @param originalData Dados originais da linha
     * @return Nova instância de ExcelRowError
     */
    public static ExcelRowError of(int rowNumber, String errorMessage, Map<String, String> originalData) {
        return new ExcelRowError(rowNumber, errorMessage, originalData);
    }
    
    /**
     * Obtém um valor específico dos dados originais.
     * 
     * @param columnName Nome da coluna
     * @return Valor da coluna ou string vazia se não encontrada
     */
    public String getOriginalValue(String columnName) {
        return originalData.getOrDefault(columnName, "");
    }
    
    /**
     * Verifica se a linha contém dados para uma coluna específica.
     * 
     * @param columnName Nome da coluna
     * @return true se a coluna existe e não está vazia
     */
    public boolean hasValue(String columnName) {
        String value = originalData.get(columnName);
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Retorna uma representação textual do erro para logs.
     * 
     * @return String formatada com número da linha e erro
     */
    public String toLogString() {
        return String.format("Linha %d: %s", rowNumber, errorMessage);
    }
}